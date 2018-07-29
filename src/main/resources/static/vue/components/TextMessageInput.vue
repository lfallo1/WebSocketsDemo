<template>
    <div>
        <div id="input-container" class="input-group" v-if="auth.name && isTranscriber">
                <span id="toggle-color-button" class="input-group-addon" @click="toggleColor"><span
                        class="glyphicon glyphicon-retweet"></span>&nbsp;Change sender</span>
            <input class="form-control" :disabled="channelSubscriptions.length == 0" type="text"
                   @keydown="nextCharacter"
                   v-model="currentMessage"></input>
            <span id="send-button" :class="'input-group-addon btn bg-' + textColor"
                  :disabled="channelSubscriptions.length == 0" @click="carriageReturn">Send</span>
        </div>
        <div id="current-line" :class="'text-' + currentLine.color">{{currentLine.value}}</div>
    </div>
</template>

<script>

    import {mapState, mapGetters, mapActions} from 'vuex';

    export default {
        data() {
            return {
                testMessage: ''
            }
        },
        methods: {
            ...mapActions({
                sendMessage: 'chat/sendMessage',
                toggleColor: 'chat/toggleColor',
                showMessage: 'chat/showMessage',
                addMessage: 'chat/addMessage',
                updateCurrentMessage: 'chat/updateCurrentMessage',
                scroll: 'scroll'
            }),
            send(from, msg) {
                const channel = {
                    channelId: this.channelSubscriptions[0].channel.channelId,
                    name: this.channelSubscriptions[0].channel.name
                };
                this.sendMessage({from: from, msg: msg, channel: channel, color: this.textColor});
            },
            nextCharacter(e) {
                if (this.isCharacterKeyPress(e)) {
                    this.send(this.auth.name, e.key);
                }
            },

            carriageReturn() {
                this.send(this.auth.name, 'enter');
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
            currentMessage: {
                get() {
                    return this.$store.state.chat.currentMessage
                },
                set(value) {
                    this.updateCurrentMessage(value);
                }
            },
            ...mapState({
                auth: state => state.auth,
                channelSubscriptions: state => state.chat.channelSubscriptions,
                channelParticipants: state => state.chat.channelParticipants,
                color: state => state.chat.color,
                currentLine: state => state.chat.currentLine
            }),
            ...mapGetters({
                isTranscriber: 'chat/isTranscriber',
                textColor: 'chat/textColor'
            })
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
        white-space: pre;
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

    #input-container {
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