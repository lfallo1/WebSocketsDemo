<template>
    <div id="chat-window-container">
        <!--<div id="chat-window" class="text-center">-->
            <!--<div>-->
                <!--<li class="list-group-item" :class="message.textClass" v-for="message in messages">{{message.data}}</li>-->
            <!--</div>-->
            <!--<div :class="connected ? 'text-success' : 'text-danger'">{{connected ? 'Connected' : 'Disconnected'}}</div>-->

            <!--<input type="text" @keydown="nextCharacter" v-model="currentMessage"></input>-->
            <!--<button @click="carriageReturn">Send</button>-->
            <!--<div id="current-line" :class="textColor">{{currentLine}}</div>-->

            <!--<button v-if="connected" @click="disconnect">Disconnect</button>-->
            <!--<button v-else @click="connect">Connect</button>-->
        <!--</div>-->
    </div>
</template>

<script>

    import Stomp from 'stompjs';
    import config from './config.js';

    export default{
        data () {
            return {
                messages: [],
                currentMessage: "",
                currentLine: "",
                connected: false,
                color: false,
                stompClient: {},
                csrf: ""
            }
        },
        methods: {

            connect() {
                var socket = new SockJS('/name');
                this.stompClient = Stomp.over(socket);
                this.stompClient.connect({'X-CSRF-TOKEN': this.csrf}, (frame) => {
                    this.connected = true
                    console.log('Connected: ' + frame);
                    this.stompClient.subscribe('/topic/messages', (data) => {
                        this.showMessage(data);
                    });
                });
            },

            disconnect() {
                if (this.stompClient != null) {
                    this.stompClient.disconnect();
                }
                this.connected = false;
            },

            nextCharacter(e){
                if (this.isCharacterKeyPress(e)) {
                    this.send('lfallo1',e.key);
                }
            },

            carriageReturn() {
                this.send('lfallo1', 'enter');
            },

            showMessage(data) {

                let value = JSON.parse(data.body).text;
                if(value.toLowerCase() == 'enter') {
                    this.messages.push({data: this.currentLine, textClass: this.textColor})
                    this.currentLine = "";
                    this.currentMessage = "";
                    this.color = !this.color;
                }
                else if(value.toLowerCase() == 'backspace'){
                    if(this.currentLine.length > 0){
                        this.currentLine = this.currentLine.substring(0,this.currentLine.length-1)
                    }
                } else{
                    this.currentLine += value
                }

            },

            send(from, msg){
                this.stompClient.send("/app/name", {},
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
            textColor(){
                return this.color ? 'text-info' : 'text-warning'
            }
        },
        created(){
            this.csrf = config.getCsrfHeader();
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
</style>
