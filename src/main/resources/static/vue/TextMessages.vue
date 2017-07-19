<template>
    <div id="chat-window-container">
        <div id="chat-window" class="text-center">
            <div>
                <li class="list-group-item" :class="message.textClass" v-for="message in messages">{{message.data}}</li>
            </div>

            <div id="input-container" v-if="auth.name">
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
                <h3 v-if="auth.name">Select feed</h3>
                <h3 v-else>Subscribe to feed</h3>
                <div class="btn-group text-center" role="group">
                    <button :class="{'active' : subscribed.indexOf(channel) > -1}" class="btn btn-default"
                            @click="toggleSubscription(channel)" v-for="channel in channels">{{channel}}
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
                channels: ['msdn', 'traffic', 'lrpu']
            }
        },
        methods: {

            connect() {
                var socket = new SockJS('/name');
                this.stompClient = Stomp.over(socket);
                this.stompClient.connect({'X-CSRF-TOKEN': this.csrf}, (frame) => {
                    eventBus.$emit('connected', {value: true});
                    console.log('Connected: ' + frame);
                });
            },

            toggleSubscription(channel) {

                if (this.subscribed.indexOf(channel) > -1) {
                    this.stompClient.unsubscribe('/topic/messages/' + channel);
                    eventBus.$emit('unsubscribe', {value: channel});
                } //for now disallow multiple channels (This is temporary)
                else if (this.subscribed.length > 0) {
                    return;
                }
                else if (this.stompClient.subscribe) {
                    this.stompClient.subscribe('/topic/messages/' + channel, (data) => {
                        this.showMessage(data);
                    });
                    this.stompClient.subscribe('/topic/users/' + channel, (data) => {
                        console.log("#participants has been updated", data);
                    });
                    eventBus.$emit('addSubscription', {value: channel});
                }
            },

            handleUnsubscribe(data) {
                for (let i = 0; i < this.subscribed.length; i++) {
                    if (this.subscribed[i] === data.value) {
                        this.subscribed.splice(i, 1);
                        return;
                    }
                }
            },

            disconnect() {
                if (this.stompClient != null) {
                    this.stompClient.disconnect();
                }
                eventBus.$emit('connected', {value: false});
                eventBus.$emit('clearSubscribed');
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
                this.stompClient.send("/app/name/" + this.subscribed[0], {},
                    JSON.stringify({'from': from, 'text': msg}));
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
            }
        },
        created() {

            //setup event bus handlers
            eventBus.$on('connected', (data) => this.connected = data.value);
            eventBus.$on('addSubscription', (data) => this.subscribed.push(data.value));
            eventBus.$on('unsubscribe', this.handleUnsubscribe);
            eventBus.$on('clearSubscribed', () => this.subscribed = []);
            eventBus.$on('auth', (data) => this.auth = data.value);

            this.csrf = config.getCsrfHeader();
            axios.get('api/user')
                .then(res => {
                    eventBus.$emit('auth', {value: res.data});
                })
                .catch(err => {
                    console.log("Not logged in")
                });
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
