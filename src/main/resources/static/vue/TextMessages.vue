<template>
    <div id="chat-window-container">
        <div id="chat-window" class="text-center container">

            <div id="chat-row-container" class="row">
                <div id="chat-participants-container" class="col-md-3">
                    <div class="panel panel-primary" v-show="loggedInParticipants.length > 0">
                        <div class="panel-heading">Channel participants</div>
                        <div class="panel-body">
                            <div class="channel-participant"
                                 v-for="participant in loggedInParticipants">
                                {{participant.user.name}}
                                <i v-if="auth.name">{{auth.name == participant.user.name ? ' (self)' : ''}}</i>
                                <small v-if="participant.transcriber"><span
                                        class="text-primary glyphicon glyphicon-user"> [Transcriber]</span>
                                </small>
                                <hr>
                            </div>
                        </div>
                    </div>

                    <div id="no-transcriber-warning" class="alert alert-danger text-center"
                         v-if="subscribed.length > 0 && !hasTranscriber">
                        <span class="glyphicon glyphicon-info-sign"></span>&nbsp;
                        There is not currently a transcriber for this channel
                    </div>

                    <div id="already-subscriber-warning" class="alert alert-warning text-center"
                         v-if="subscribed.length > 0 && otherTranscriberExists">
                        <span class="glyphicon glyphicon-info-sign">&nbsp;</span>
                        A logged in user is already transcribing on this channel. You will be able to listen, but not transcribe
                    </div>
                </div>

                <div class="col-md-8">
                    <li class="chat-list-item list-group-item" :class="'text-' + message.textClass"
                        v-for="message in messages">
                        <div class="message-label">
                            <small :class="'text-' + message.textClass">
                                {{message.data.channel}}&nbsp;{{message.data.time}}
                            </small>
                            <div class="chat-list-item-message-text">{{message.data.value}}</div>
                        </div>
                    </li>
                    <div ref="scrollTarget"></div>
                </div>
            </div>

            <div id="input-container" class="input-group" v-if="auth.name && isTranscriber">
                <span id="toggle-color-button" class="input-group-addon" @click="toggleColor"><span
                        class="glyphicon glyphicon-retweet"></span>&nbsp;Change sender</span>
                <input class="form-control" :disabled="subscribed.length == 0" type="text" @keydown="nextCharacter"
                       v-model="currentMessage"></input>
                <span id="send-button" :class="'input-group-addon btn bg-' + textColor"
                      :disabled="subscribed.length == 0" @click="carriageReturn">Send</span>
            </div>
            <div id="current-line" :class="'text-' + currentLine.color">{{currentLine.value}}</div>

            <br>

            <div v-if="subscribed.length == 0">
                <div id="subscription-list" v-if="connected">
                    <h3>Listen to feed</h3>
                    <div class="btn-group text-center" role="group">
                        <button :class="{'active' : subscribed.filter(s=>s.channel == channel).length > 0}"
                                class="btn btn-default"
                                @click="toggleSubscription(channel)" v-for="channel in channelsListen">{{channel.name}}
                        </button>
                    </div>
                </div>

                <div id="transcribe-list" v-if="auth.name && connected">
                    <h3>Transcribe to feed</h3>
                    <div class="btn-group text-center" role="group">
                        <button :class="{'active' : subscribed.filter(s=>s.channel == channel).length > 0}"
                                class="btn btn-default"
                                @click="toggleSubscription(channel)" v-for="channel in channelsTranscribe">
                            {{channel.name}}
                        </button>
                    </div>
                </div>
            </div>

            <div v-else>
                <button class="btn btn-danger" @click="unsubscribe">Stop listening / transcribing</button>
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
                currentLine: {value: "", channel: "", color: ""},
                connected: false,
                color: false,
                stompClient: {},
                csrf: "",
                auth: {},
                transcribing: false,
                subscribed: [],
                channelParticipants: [],
                channels: [],
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

            unsubscribe() {
                if (this.subscribed.length > 0) {
                    const currentSubscription = this.subscribed[0];
                    eventBus.$emit('unsubscribe', {value: currentSubscription.channel});
                }
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

                    let sub = this.stompClient.subscribe('/topic/channelcount/' + channel.channelId, (data) => {
                        console.log("#participants has been updated", data);
                        let channelParticipants = JSON.parse(data.body)
                        eventBus.$emit('channelParticipants', {value: channelParticipants});
                    });
                    subscription.endpoints.push(sub)

                    sub = this.stompClient.subscribe('/topic/transcription/' + channel.channelId, (data) => {
                        this.showMessage(data);
                    });
                    subscription.endpoints.push(sub)

                    eventBus.$emit('addSubscription', {value: subscription});
                }
            },

            handleUnsubscribe(data) {
                const channel = data.value;
                const subscription = this.subscribed.filter(s => s.channel.channelId == channel.channelId)[0];
                for (let i = 0; i < subscription.endpoints.length; i++) {
                    subscription.endpoints[i].unsubscribe();
                }

                for (let i = 0; i < this.subscribed.length; i++) {

                    if (this.subscribed[i].channel.channelId === channel.channelId) {
                        this.subscribed.splice(i, 1);
                        break;
                    }
                }
                eventBus.$emit('updateSubscribed', {value: []});
                eventBus.$emit('clearChannelParticipants');
            },

            disconnect() {
                if (this.stompClient != null) {
                    this.stompClient.disconnect();
                }
                eventBus.$emit('connected', {value: false});
                eventBus.$emit('updateSubscribed', {value: []});
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
                let author = JSON.parse(data.body).from;
                let value = JSON.parse(data.body).text;
                let color = JSON.parse(data.body).color;
                let channel = JSON.parse(data.body).channel;
                let time = JSON.parse(data.body).time;
                console.log("received message from channel: " + channel.toString());
                if (value.toLowerCase() == 'enter') {
                    if (this.currentLine.value) {
                        this.currentLine.channel = channel.name;
                        this.currentLine.time = time;
                        this.currentLine.author = author;
                        this.messages.push({data: this.currentLine, textClass: color})
                        this.currentLine = {value: "", channel: "", author: "", time: undefined};
                        this.currentMessage = "";
                        this.$scrollTo(this.$refs.scrollTarget, 500)
                    }
                    eventBus.$emit('toggleColor', {value: !this.color});
                }
                else if (value.toLowerCase() == 'backspace') {
                    if (this.currentLine.value.length > 0) {
                        this.currentLine.color = color;
                        this.currentLine.value = this.currentLine.value.substring(0, this.currentLine.value.length - 1)
                    }
                } else {
                    this.currentLine.color = color;
                    this.currentLine.value += value;
                }

            },

            send(from, msg) {
                const channel = {
                    channelId: this.subscribed[0].channel.channelId,
                    name: this.subscribed[0].channel.name
                };
                this.stompClient.send("/app/shared/" + this.subscribed[0].channel.channelId, {},
                    JSON.stringify({'from': from, 'text': msg, 'channel': channel, 'color': this.textColor}));
            },

            sendDirect(username) {

                if (this.auth.name && username != this.auth.name) {

                    const unique = (Date.now().toString(36) + Math.random().toString(36).substr(2, 5)).toUpperCase();

                    this.directChannels.push(this.stompClient.subscribe('/topic/direct/message/' + unique, (data) => {
                        console.log(data);
                    }));

                    this.stompClient.send("/app/direct/request/" + username, {},
                        JSON.stringify({
                            'from': this.auth.name,
                            'text': unique,
                            'channel': {name: username}
                        }));

                    setTimeout(() => {

                        this.stompClient.send("/app/direct/message/" + unique, {},
                            JSON.stringify({
                                'from': this.auth.name,
                                'text': "Hey there, " + username + ". Had a quick question?",
                                'channel': {name: unique}
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
            },
            toggleColor() {
                eventBus.$emit('toggleColor', {value: !this.color});
            }
        },
        computed: {
            channelsListen() {
                return this.channels.filter(c => !c.transcribers || c.transcribers.length == 0);
            },
            channelsTranscribe() {
                return this.channels.filter(c => c.transcribers && c.transcribers.length > 0);
            },
            textColor() {
                return this.color ? 'info' : 'warning'
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
            },
            hasTranscriber() {
                for (let i = 0; i < this.channelParticipants.length; i++) {
                    if (this.channelParticipants[i].transcriber) {
                        return true;
                    }
                }
                return false;
            },
            otherTranscriberExists() {
                if (this.auth.name && this.hasTranscriber) {
                    for (let i = 0; i < this.channelParticipants.length; i++) {
                        if (this.channelParticipants[i].user && this.channelParticipants[i].user.name == this.auth.name
                            && this.channelParticipants[i].authenticatedToTranscribe
                            && !this.channelParticipants[i].transcriber) {
                            return true;
                        }
                    }
                }
                return false;
            }
        },
        created() {

            //setup event bus handlers
            eventBus.$on('connect', () => this.connect());
            eventBus.$on('disconnect', () => this.disconnect());

            eventBus.$on('connected', (data) => this.connected = data.value);
            eventBus.$on('addSubscription', (data) => this.subscribed.push(data.value));
            eventBus.$on('unsubscribe', this.handleUnsubscribe);
            eventBus.$on('updateSubscribed', (data) => this.subscribed = data.value);
            eventBus.$on('auth', (data) => this.auth = data.value);
            eventBus.$on('channelParticipants', (data) => {
                this.channelParticipants = data.value
            });
            eventBus.$on('clearChannelParticipants', (data) => this.channelParticipants = []);
            eventBus.$on('channels', (data) => this.channels = data.value);
            eventBus.$on('toggleColor', (data) => {
                this.color = data.value
            });

            this.csrf = config.getCsrfHeader();
            axios.get('api/user')
                .then(res => eventBus.$emit('auth', {value: res.data}))
                .catch(err => console.log("Not logged in"));

            axios.get('api/channel')
                .then(res => {
                    eventBus.$emit('channels', {value: res.data})
                })
                .catch(err => console.log("Error loading channels"));

            eventBus.$emit('connect');
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

    #subscription-list .btn, #transcribe-list .btn {
        color: #428bca;
    }

    #subscription-list h3, #transcribe-list h3 {
        color: #428bca;
        font-size: 20px;
        font-weight: bold;
    }

    #chat-window input {
        height: 40px;
        border-radius: 4px;
        border: 1px solid rgba(0, 0, 0, 0.3);
        background: white;
        font-size: 26px;
        padding-left: 10px;
        font-weight: bold;
    }

    #chat-window .message-label.pull-left span {
        font-size: 13px;
    }

    #chat-row-container .channel-participant {
        margin-top: -10px;
    }

    #chat-row-container .channel-participant hr {
        margin-top: 5px;
    }

    .chat-list-item {
        text-align: left;
    }

    .chat-list-item small {
        font-size: 12px;
        opacity: 0.65;
    }

    li.chat-list-item {
        padding: 2px 8px 2px 8px;
    }

    .chat-list-item-message-text {
        margin-top: -7px;
        font-weight: bold;
    }

    #chat-window input:focus {
        outline-width: 0;
    }

    #toggle-color-button.input-group-addon:hover {
        cursor: pointer;
        background: #555;
        color: #eee;
    }

    .bg-info {
        background: #00aedb;
        color: white;
        border: none;
    }

    .bg-warning {
        background: #f37735;
        color: white;
        border: none;
    }

    #input-container{
        width: 75%;
        margin: 0px auto;
        margin-top: 25px;
    }
</style>
