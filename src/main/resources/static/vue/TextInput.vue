<template>
    <div id="chat-window-container">
        <div id="chat-window" class="text-center">
            <div>
                <li class="list-group-item" :class="message.textClass" v-for="message in messages">{{message.data}}</li>
            </div>
            <div :class="connected ? 'text-success' : 'text-danger'">{{connected ? 'Connected' : 'Disconnected'}}</div>

            <input type="text" @keydown="nextCharacter" v-model="currentMessage"></input>
            <button @click="carriageReturn">Send</button>
            <div id="current-line" :class="textColor">{{currentLine}}</div>

            <button v-if="connected" @click="disconnect">Disconnect</button>
            <button v-else @click="connect">Connect</button>
        </div>
    </div>
</template>

<script>
    export default{
        data () {
            return {
                messages: [],
                currentMessage: "",
                currentLine: "",
                connected: false,
                color: false
            }
        },
        methods: {
            nextCharacter(e){
                if(this.isCharacterKeyPress(e)){
                    this.ws.send(e.key);
                }
            },
            connect() {
                this.ws = new WebSocket('ws://localhost:8080/name');
                this.ws.onmessage = this.onMessage;
                this.connected = true;
                this.ws.onClose = this.disconnect;
                this.ws.onerror = (err) => console.log("ERROR: " + err);
            },

            disconnect() {
                if (this.ws != null) {
                    this.ws.close();
                }
                this.connected = false;
                console.log("Disconnected");
            },

            carriageReturn() {
                this.ws.send("enter");
            },

            onMessage(char) {
                let value = char.data;
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
            isCharacterKeyPress(e) {
                var keycode = e.keyCode;

                var valid =
                    (keycode > 47 && keycode < 58)   || // number keys
                    (keycode == 32 || keycode == 13 || keycode == 8)   || // spacebar & return key(s) (if you want to allow carriage returns)
                    (keycode > 64 && keycode < 91)   || // letter keys
                    (keycode > 95 && keycode < 112)  || // numpad keys
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
            this.connect()
        }
    }
</script>

<style scoped>
    #current-line{
        padding: 12px;
        font-weight: bold;
        font-size: 22px;
    }

    li{
        font-size: 20px;
    }
</style>
