<template>
    <div>
        <div ref="directChatRef"></div>
        <div class="direct-chat-sessions" v-show="directChatSessions.length > 0"
             v-for="session in directChatSessions">
            <div v-show="!session.isHidden">
                <div class="direct-chat-session-container" v-show="session.visible">
                    <div>
                        <div class="direct-chat-session-usernames" >
                            <span @click="session.visible = false">Chat session: {{session.directChatUsernames.toString()}}</span>
                            <span class="glyphicon glyphicon-remove text-danger"
                                  @click="closeDirectChatSession(session)"></span>
                        </div>
                        <div v-if="session.visible">
                            <div :class="{'message-self':message.author == auth.name, 'message-other':message.author != auth.name}"
                                 class="direct-chat-session-message" v-for="message in session.directChatMessages">
                                <small>{{message.author}} ({{message.time}})</small>&nbsp; {{message.text}}
                            </div>
                            <input class="direct-chat-session-input form-control" type="text"
                                   v-model="session.directChatInputText" @keydown.enter="sendDirectTextMessage(session)"/>
                            <button class="direct-chat-session-send-btn btn btn-sm btn-default"
                                    @click="sendDirectTextMessage(session)">Send
                            </button>
                        </div>
                    </div>
                </div>
                <div class="direct-chat-session-container-collapsed" @click="session.visible=true" v-show="!session.visible">
                    Chat session: {{session.directChatUsernames.toString()}}&nbsp;<span class="glyphicon glyphicon-plus"></span>
                </div>
            </div>
        </div>
    </div>
</template>

<script>

    import {eventBus} from '../main.js';

    export default {
        data() {
            return {
                auth: {},
                directChatSessions: []
            }
        },
        methods: {
            closeDirectChatSession(session) {
                console.log('closing window');
                session.isHidden = true;
            },
            handleDirectMessage(data) {
                let author = JSON.parse(data.body).from;
                let text = JSON.parse(data.body).text;
                let channel = JSON.parse(data.body).channel;
                let time = JSON.parse(data.body).time;
                const message = {
                    'author': author,
                    'text': text,
                    'channel': channel,
                    'time': time
                };

                let session = this.directChatSessions.filter(s => s.directChatChannel == channel.name)[0];
                session.directChatMessages.push(message);
                session.isHidden = false;
            },
            sendDirectTextMessage(session) {
                eventBus.$emit('sendDirectTextMessage', {
                    value: {
                        text: session.directChatInputText,
                        channel: session.directChatChannel
                    }
                });
                session.directChatInputText = "";
            },
            handleDirectChatRequest(data) {
                //{value: {username: username, channel: unique}}
                this.directChatSessions.push({
                    directChatUsernames: [data.value.username, this.auth.name],
                    directChatChannel: data.value.channel,
                    directChatMessages: [],
                    directChatInputText: "",
                    showDirectChat: false,
                    visible: true,
                    isHidden: false
                });
            },
            handleTryStartDirectChat(data) {
                const username = data.value;
                const session = this.hasExistingChat(username);
                if(session){
                    session.isHidden = false;
                }
                else if (this.auth.name && username != this.auth.name) {
                    eventBus.$emit('directChatRequestInvocation', {value: username});
                }
                setTimeout(()=> this.$scrollTo(this.$refs.directChatRef, 500), 100);
            },
            hasExistingChat(username) {
                for (let i = 0; i < this.directChatSessions.length; i++) {
                    for (let j = 0; j < this.directChatSessions[i].directChatUsernames.length; j++) {
                        if (this.directChatSessions[i].directChatUsernames[j] == username) {
                            return this.directChatSessions[i];
                        }
                    }
                }
                return undefined;
            }
        },
        created() {
            eventBus.$on('handleDirectMessage', this.handleDirectMessage);
            eventBus.$on('directChatRequest', this.handleDirectChatRequest);
            eventBus.$on('tryStartDirectChat', this.handleTryStartDirectChat);
            eventBus.$on('auth', (data) => this.auth = data.value);

            eventBus.$on('connected', (data) => {
                if(!data.value){
                    this.directChatSessions = [];
                }
            });

            eventBus.$on('disconnectUser', (data) => {
                const user = data.value.body;
                for (var i = 0; i < this.directChatSessions.length; i++) {
                    if (this.directChatSessions[i].directChatUsernames.indexOf(user) > -1) {
                        this.directChatSessions.splice(i, 1);
                        console.log('Closed direct chat session')
                        break;
                    }
                }
            });
        }
    }
</script>

<style scoped>

    .direct-chat-session-usernames {
        background: rgb(51,122,183);
        width: 105%;
        margin-left: -2.5%;
        margin-top: -10px;
        margin-bottom: 35px;
        padding: 7px;
        text-align: left;
        font-weight: bold;
    }

    .direct-chat-session-usernames .glyphicon {
        float: right;
        font-size: 20px;
    }

    .direct-chat-session-usernames .glyphicon:hover {
        cursor: pointer;
        opacity: 0.7;
    }

    .direct-chat-session-message {
        background: #222;
        margin: 8px auto;
        padding: 8px;
        width: 65%;
    }

    .direct-chat-session-message small {
        color: rgb(51,122,183);

    }

    .direct-chat-session-message.message-other {
        background: rgb(51,122,183) !important;
        color: #111;
    }

    .message-other small {
        color: white;
    }

    .direct-chat-session-container {
        padding: 8px;
        background: #555;
        color: white;
        max-height: 200px;
        overflow-y: scroll;
    }

    .direct-chat-session-container input {
        margin-top: 25px;
        width: 80%;
    }

    .direct-chat-session-container .btn, .direct-chat-session-container input {
        display: inline-block !important;
    }

    .direct-chat-session-send-btn {
        font-weight: bold;
        padding: 7px;
        margin-top: -3px;
    }

    .direct-chat-session-send-btn {
        font-weight: bold;
        padding: 7px;
        margin-top: -3px;
    }

    .message-self {
        float: right;
        margin-right: 12px !important;
    }

    .message-other {
        float: left;
        margin-left: 12px !important;
    }

    .direct-chat-session-container-collapsed{
        background: rgb(51,122,183);
        color: white;
        float: right;
        padding: 5px;
    }

    .direct-chat-session-container-collapsed:hover, .direct-chat-session-usernames:hover{
        cursor: pointer;
        opacity: 0.7;
    }

    .direct-chat-sessions{
        width: 300px;
        max-height: 225px;
        overflow-y: scroll;
    }

    .direct-chat-session-container-collapsed{
        border: 1px solid #777;
        width: 300px;
    }
</style>