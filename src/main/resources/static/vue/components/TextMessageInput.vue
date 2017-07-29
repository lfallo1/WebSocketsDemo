<template>
    <div>
        <div id="input-container" class="input-group" v-if="auth.name && isTranscriber">
                <span id="toggle-color-button" class="input-group-addon" @click="toggleColor"><span
                        class="glyphicon glyphicon-retweet"></span>&nbsp;Change sender</span>
            <input class="form-control" :disabled="subscribed.length == 0" type="text" @keydown="nextCharacter"
                   v-model="currentMessage"></input>
            <span id="send-button" :class="'input-group-addon btn bg-' + textColor"
                  :disabled="subscribed.length == 0" @click="carriageReturn">Send</span>
        </div>
        <div id="current-line" :class="'text-' + currentLine.color">{{currentLine.value}}</div>
    </div>
</template>

<script>

    import {eventBus} from '../main.js';

    export default{
        data(){
            return {
                currentMessage: "",
                currentLine: {value: "", channel: "", color: ""},
                subscribed: [],
                color: false,
                auth: {},
                channelParticipants: []
            }
        },
        methods: {
            send(from, msg) {
                const channel = {
                    channelId: this.subscribed[0].channel.channelId,
                    name: this.subscribed[0].channel.name
                };
                eventBus.$emit('send', {value: {from: from, msg: msg, channel: channel, color: this.textColor}});
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
                        eventBus.$emit('addMessage', {value: {data: this.currentLine, textClass: color}})
                        this.currentLine = {value: "", channel: "", author: "", time: undefined};
                        this.currentMessage = "";
                        eventBus.$emit('scroll');
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
            textColor() {
                return this.color ? 'info' : 'warning'
            }
        },
        created(){
            //setup event bus handlers
            eventBus.$on('toggleColor', (data) => {
                this.color = data.value
            });
            eventBus.$on('auth', (data)=>{
                this.auth = data.value
            });
            eventBus.$on('channelParticipants', (data) => {
                this.channelParticipants = data.value
            });
            eventBus.$on('clearChannelParticipants', (data) => this.channelParticipants = []);
            eventBus.$on('addSubscription', (data) => this.subscribed.push(data.value));
            eventBus.$on('updateSubscribed', (data) => this.subscribed = data.value);
            eventBus.$on('showMessage', (data) => this.showMessage(data.value));
        }
    }
</script>

<style scoped>
    #toggle-color-button.input-group-addon:hover {
        cursor: pointer;
        background: #555;
        color: #eee;
    }
    #current-line {
        padding: 12px;
        font-weight: bold;
        font-size: 22px;
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
    #input-container{
        width: 75%;
        margin: 0px auto;
        margin-top: 25px;
    }
    #chat-window input:focus {
        outline-width: 0;
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
</style>