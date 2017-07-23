<template>
    <div id="chat-window-container">
        <div id="chat-window" class="text-center">

            <div v-if="auth.name" class="well well-sm" v-show="loggedInParticipants.length > 0">
                <div class="channel-participant"
                     v-for="participant in loggedInParticipants"
                     @click="sendDirect(participant.user.name)">
                    {{participant.user.name}}
                    <i v-if="auth.name">{{auth.name == participant.user.name ? ' (self)' : ''}}</i>
                    <small v-if="participant.transcriber"><span class="text-primary glyphicon glyphicon-user"> [Transcriber]</span>
                    </small>
                </div>
            </div>

            <div>
                <li class="list-group-item" :class="message.textClass" v-for="message in messages">{{message.data}}</li>
            </div>

            <div id="input-container" v-if="auth.name && isTranscriber">
                <input :disabled="subscribed.length == 0" type="text" @keydown="nextCharacter"
                       v-model="currentMessage"></input>
                <button class="btn btn-primary" :disabled="subscribed.length == 0" @click="carriageReturn">Send</button>
            </div>
            <div id="current-line" :class="textColor">{{currentLine}}</div>

            <button class="btn btn-danger" v-if="connected" @click="disconnect"><span
                    class="glyphicon glyphicon-remove"></span>Disconnect
            </button>
            <button v-else class="btn btn-success" @click="connect"><span class="glyphicon glyphicon-signal"></span>&nbsp;Connect to server
            </button>

            <br>

            <div id="subscription-list" v-if="connected">
                <h3>Listen to feed</h3>
                <div class="btn-group text-center" role="group">
                    <button :class="{'active' : subscribed.filter(s=>s.channel == channel).length > 0}"
                            class="btn btn-default"
                            @click="toggleSubscription(channel, false)" v-for="channel in channels">{{channel}}
                    </button>
                </div>
            </div>

            <div id="transcribe-list" v-if="auth.name">
                <h3>Listen / Transcribe to feed</h3>
                <div class="btn-group text-center" role="group">
                    <button :class="{'active' : subscribed.filter(s=>s.channel == channel).length > 0}"
                            class="btn btn-default"
                            @click="toggleSubscription(channel, true)" v-for="channel in transcribeChannels">{{channel}}
                    </button>
                </div>
            </div>

        </div>
    </div>
</template>

<script>

    import Stomp from 'stompjs';
    import config from './config.js';
    import axios from 'axios';
    import {eventBus} from './main.js';

    export default {
        data() {
            return {
                messages: [],
                currentMessage: "",
                currentLine: "",
                connected: false,
                color: false,
                stompClient: {},
                csrf: "",
                auth: {},
                transcribing: false,
                subscribed: [],
                channelParticipants: [],
                channels: [],
                transcribeChannels: [],
                directChannels: []
            }
        },
        methods: {

            connect() {
                var socket = new SockJS('/shared');
                this.stompClient = Stomp.over(socket);
                this.stompClient.connect({'X-CSRF-TOKEN': this.csrf}, (frame) => {
                    eventBus.$emit('connected', {value: true});
                    console.log('Connected: ' + frame);

                    if (this.auth.name) {
                        this.stompClient.subscribe('/topic/direct/request/' + this.auth.name, (data) => {
                            this.directChannels.push(this.stompClient.subscribe('/topic/direct/message/' + JSON.parse(data.body).text, (data) => {
                                console.log(data);
                            }));
                        });
                    }

                });
            },

            toggleSubscription(channel, shouldTranscribe) {

                if (this.stompClient.subscribe) {


                    //i don't think its a good idea to allow multiple sessions simultaneously.
                    if (this.subscribed.length > 0) {
                        const currentSubscription = this.subscribed[0];
                        eventBus.$emit('unsubscribe', {value: currentSubscription.channel});
                    }

                    let subscription = {
                        endpoints: [],
                        channel: channel
                    };

                    let sub = this.stompClient.subscribe('/topic/channelcount/' + channel, (data) => {
                        console.log("#participants has been updated", data);
                        let channelParticipants = JSON.parse(data.body)
                        eventBus.$emit('channelParticipants', {value: channelParticipants});
                    });
                    subscription.endpoints.push(sub)

                    sub = this.stompClient.subscribe('/topic/transcription/' + channel, (data) => {
                        this.showMessage(data);
                    });
                    subscription.endpoints.push(sub)

                    eventBus.$emit('addSubscription', {value: subscription});
                }
            },

            handleUnsubscribe(data) {
                const channel = data.value;
                const subscription = this.subscribed.filter(s => s.channel == channel)[0];
                for (let i = 0; i < subscription.endpoints.length; i++) {
                    subscription.endpoints[i].unsubscribe();
                }

                for (let i = 0; i < this.subscribed.length; i++) {

                    if (this.subscribed[i].channel === channel) {
                        this.subscribed.splice(i, 1);
                        return;
                    }
                }

                eventBus.$emit('clearChannelParticipants');
            },

            disconnect() {
                if (this.stompClient != null) {
                    this.stompClient.disconnect();
                }
                eventBus.$emit('connected', {value: false});
                eventBus.$emit('clearSubscribed');
                eventBus.$emit('clearChannelParticipants');
            },

            nextCharacter(e) {
                if (this.isCharacterKeyPress(e)) {
                    this.send(this.auth.name, e.key);
                }
            },

            carriageReturn() {
                this.send(this.auth.name, 'enter');
            },

            showMessage(data) {

                let value = JSON.parse(data.body).text;
                let channel = JSON.parse(data.body).channel;
                if (value.toLowerCase() == 'enter') {
                    this.messages.push({data: this.currentLine, textClass: this.textColor})
                    this.currentLine = "";
                    this.currentMessage = "";
                    this.color = !this.color;
                }
                else if (value.toLowerCase() == 'backspace') {
                    if (this.currentLine.length > 0) {
                        this.currentLine = this.currentLine.substring(0, this.currentLine.length - 1)
                    }
                } else {
                    this.currentLine += value
                }

            },

            send(from, msg) {
                this.stompClient.send("/app/shared/" + this.subscribed[0].channel, {},
                    JSON.stringify({'from': from, 'text': msg}));
            },

            sendDirect(username) {

                if (this.auth.name && username != this.auth.name) {

                    const unique = (Date.now().toString(36) + Math.random().toString(36).substr(2, 5)).toUpperCase();

                    this.directChannels.push(this.stompClient.subscribe('/topic/direct/message/' + unique, (data) => {
                        console.log(data);
                    }));

                    this.stompClient.send("/app/direct/request/" + username, {},
                        JSON.stringify({'from': this.auth.name, 'text': unique}));

                    setTimeout(() => {

                        this.stompClient.send("/app/direct/message/" + unique, {},
                            JSON.stringify({
                                'from': this.auth.name,
                                'text': "Hey there, " + username + ". Had a quick question?"
                            }));
                    }, 1000);
                }
            },

            isCharacterKeyPress(e) {
                var keycode = e.keyCode;

                var valid =
                    (keycode > 47 && keycode < 58) || // number keys
                    (keycode == 32 || keycode == 13 || keycode == 8) || // spacebar & return key(s) (if you want to allow carriage returns)
                    (keycode > 64 && keycode < 91) || // letter keys
                    (keycode > 95 && keycode < 112) || // numpad keys
                    (keycode > 185 && keycode < 193) || // ;=,-./` (in order)
                    (keycode > 218 && keycode < 223);   // [\]' (in order)

                return valid;
            }
        },
        computed: {
            textColor() {
                return this.color ? 'text-info' : 'text-warning'
            },
            loggedInParticipants() {
                return this.channelParticipants.filter(p => p.user);
            },
            isTranscriber() {
                if (!this.auth.name) {
                    return false;
                }

                for (let i = 0; i < this.channelParticipants.length; i++) {
                    if (this.channelParticipants[i].transcriber && this.channelParticipants[i].user.name == this.auth.name) {
                        return true;
                    }
                }
                return false;
            }
        },
        created() {

            //setup event bus handlers
            eventBus.$on('connected', (data) => this.connected = data.value);
            eventBus.$on('addSubscription', (data) => this.subscribed.push(data.value));
            eventBus.$on('unsubscribe', this.handleUnsubscribe);
            eventBus.$on('clearSubscribed', () => this.subscribed = []);
            eventBus.$on('auth', (data) => this.auth = data.value);
            eventBus.$on('channelParticipants', (data) => {
                this.channelParticipants = data.value
            });
            eventBus.$on('clearChannelParticipants', (data) => this.channelParticipants = []);
            eventBus.$on('channels', (data) => this.channels = data.value);
            eventBus.$on('channels_transcriber', (data) => this.transcribeChannels = data.value);


            this.csrf = config.getCsrfHeader();
            axios.get('api/user')
                .then(res => eventBus.$emit('auth', {value: res.data}))
                .catch(err => console.log("Not logged in"));

            axios.get('api/channel')
                .then(res => eventBus.$emit('channels', {value: res.data}))
                .catch(err => console.log("Error loading channels"));

            axios.get('api/channel/transcriber')
                .then(res => eventBus.$emit('channels_transcriber', {value: res.data}))
                .catch(err => console.log("Error loading transcriber channels"))
        }
    }
</script>

<style scoped>
    #current-line {
        padding: 12px;
        font-weight: bold;
        font-size: 22px;
    }

    li {
        font-size: 20px;
    }

    #subscription-list .btn {
        color: #428bca;
    }

    #subscription-list h3 {
        color: #428bca;
        font-size: 20px;
        font-weight: bold;
    }

    #chat-window input {
        width: 75%;
        height: 40px;
        border-radius: 4px;
        border: 1px solid rgba(0, 0, 0, 0.3);
        background: white;
        font-size: 26px;
        padding-left: 10px;
        font-weight: bold;
    }

    #input-container button {
        margin-left: -10px;
        margin-top: -9px;
        font-size: 20px;
        font-weight: bold;
        height: 40px;
    }

    #input-container {
        margin-top: 30px;
    }
</style>
