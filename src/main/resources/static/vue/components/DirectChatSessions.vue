<template>
    <div>
        <div v-show="directChatSessions.length > 0" v-for="session in directChatSessions">
            <div class="direct-chat-session">
                <div>
                    <p>Chat session: {{session.directChatUsernames.toString()}}</p>
                    <ul v-for="message in session.directChatMessages">
                        <li>
                            <small>{{message.author}} ({{message.time}})</small>&nbsp; {{message.text}}
                        </li>
                    </ul>
                    <input type="text" v-model="session.directChatInputText"/>
                    <button class="btn btn-sm btn-success" @click="sendDirectTextMessage(session)">Send
                    </button>
                </div>
                <button class="btn btn-sm btn-danger" @click="closeDirectChatSession(session)">End Chat</button>
            </div>
        </div>
    </div>
</template>

<script>

    import {eventBus} from '../main.js';

    export default{
        data(){
            return {
                auth: {},
                directChatSessions: [
//                    {
//                        directChatUsername: "",
//                        showDirectChat: false,
//                        directChatChannel: "",
//                        directChatInputText: "",
//                        directChatMessages: []
//                    }
                ]
            }
        },
        methods:{
            closeDirectChatSession(session){
                console.log('TODO: close the session');
            },
            handleDirectMessage(data){
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

                let session = this.directChatSessions.filter(s=>s.directChatChannel == channel.name)[0];
                session.directChatMessages.push(message);
            },
            sendDirectTextMessage(session){
                eventBus.$emit('sendDirectTextMessage', {value: {
                    text: session.directChatInputText,
                    channel: session.directChatChannel
                }});
                session.directChatInputText = "";
            },
            handleDirectChatRequest(data){
                //{value: {username: username, channel: unique}}
                this.directChatSessions.push({
                    directChatUsernames: [data.value.username, this.auth.name],
                    directChatChannel: data.value.channel,
                    directChatMessages: [],
                    directChatInputText: "",
                    showDirectChat: false
                });
            },
            handleTryStartDirectChat(data){
                const username = data.value;
                if (this.auth.name && username != this.auth.name && this.canStartDirectChat(username)) {
                    eventBus.$emit('directChatRequestInvocation', {value: username});
                }
            },
            canStartDirectChat(username){
                for(let i = 0; i < this.directChatSessions.length; i++){
                    for(let j = 0; j < this.directChatSessions[i].directChatUsernames.length; j++){
                        if(this.directChatSessions[i].directChatUsernames[j] == username){
                            return false;
                        }
                    }
                }
                return true;
            }
        },
        created(){
            eventBus.$on('handleDirectMessage', this.handleDirectMessage);
            eventBus.$on('directChatRequest', this.handleDirectChatRequest);
            eventBus.$on('tryStartDirectChat', this.handleTryStartDirectChat);
            eventBus.$on('auth', (data)=>this.auth = data.value);
        }
    }
</script>

<style scoped>

</style>