<template>
    <div>
        <div ref="directChatRef"></div>
        <div class="direct-chat-sessions" v-show="directChatSessions.length > 0"
             v-for="session in directChatSessions">
            <div v-show="!session.isHidden">
                <div :ref="'container' + session.directChatChannel"></div>
                <div class="direct-chat-session-container" v-show="session.visible">
                    <div>
                        <div class="direct-chat-session-usernames">
                            <span @click="session.visible = false">Chat session: {{session.directChatUsernames.toString()}}</span>
                            <span class="glyphicon glyphicon-remove text-danger"
                                  @click="closeDirectChatSession(session)"></span>
                        </div>
                        <div class="direct-messages-wrapper">
                            <div :ref="session.directChatChannel"></div>
                            <div v-if="session.visible" class="direct-chat-session-message" :class="{'message-self':message.author == auth.name, 'message-other':message.author != auth.name}"
                                 v-for="message in session.directChatMessages">
                                <small>{{message.author}} ({{message.time}})</small>&nbsp; {{message.text}}
                            </div>
                        </div>
                        <div class="direct-chat-session-input" v-if="session.visible">
                            <input type="text"
                                   v-model="session.directChatInputText"
                                   @keydown.enter="send(session)"/>
                            <button class="direct-chat-session-send-btn"
                                    @click="send(session)">Send
                            </button>
                        </div>
                    </div>
                </div>
                <div class="direct-chat-session-container-collapsed" @click="session.visible=true"
                     v-show="!session.visible">
                    Chat session: {{session.directChatUsernames.toString()}}&nbsp;<span
                        class="glyphicon glyphicon-plus"></span>
                </div>
            </div>
        </div>
    </div>
</template>

<script>

    import {eventBus} from '../main.js';
    import {mapState, mapGetters, mapActions} from 'vuex';

    export default {
        methods: {
            send(session){
                this.sendDirectTextMessage(session);
            },
            scroll(channel){
                let vm = this;
                vm.$scrollTo(this.$refs['container' + channel][0], 50, {
                    easing: 'ease-in',
                    onDone: ()=>{
                        vm.$scrollTo(this.$refs[channel][0], 50, {
                            container: '.direct-messages-wrapper',
                            easing: 'ease-in'
                        });
                    }
                });
            },
            ...mapActions({
                addDirectChatSession: 'chat/addDirectChatSession',
                closeDirectChatSession: 'chat/closeDirectChatSession',
                sendDirectTextMessage: 'chat/sendDirectTextMessage'
            })
        },
        computed: {
            ...mapGetters({
                hasExistingChat: 'chat/hasExistingChat'
            }),
            ...mapState({
                auth: state => state.auth,
                directChatSessions: state => state.chat.directChatSessions
            })
        },
        created(){
            eventBus.$on('scroll', (data) => this.scroll(data));
        }
    }
</script>

<style scoped>

    .direct-chat-session-usernames {
        background: rgb(51, 122, 183);
        width: 105%;
        margin-left: -2.5%;
        margin-top: -10px;
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
        color: rgb(51, 122, 183);

    }

    .direct-chat-session-message.message-other {
        background: rgb(51, 122, 183) !important;
        color: #111;
    }

    .message-other small {
        color: white;
    }

    .direct-chat-session-container {
        padding: 8px;
        background: #555;
        color: white;
        margin-bottom: 8px;
        /*max-height: 200px;*/
        /*overflow-y: scroll;*/
    }

    /*.direct-chat-sessions {*/
        /*width: 300px;*/
        /*max-height: 225px;*/
        /*overflow-y: scroll;*/
    /*}*/

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

    .direct-chat-session-container-collapsed {
        background: rgb(51, 122, 183);
        color: white;
        padding: 5px;
        margin-top: 3px;
        margin-bottom: 3px;
    }

    .direct-chat-session-container-collapsed:hover, .direct-chat-session-usernames:hover {
        cursor: pointer;
        opacity: 0.7;
    }

    .direct-chat-session-container-collapsed {
        border: 1px solid #777;
        width: 300px;
    }

    .direct-messages-wrapper{
        height: 100px !important;
        overflow-y: scroll;
    }

    .direct-chat-session-input{
        width: 105%;
        margin-left: -2.5%;
        margin-bottom: -7px;
    }

    .direct-chat-session-input input{
        width: 75%;
        float: left;
        height: 32px;
        font-weight: bold;
        color: black;
        font-size: 18px;
    }

    .direct-chat-session-input button{
        width: 25%;
        height: 32px;
        margin-top: 0px !important;
        background: #5cb85c;
        border: none;
    }

    .direct-chat-session-input button:hover{
        opacity: 0.7;
    }

    input{
        outline: none !important;
    }
</style>