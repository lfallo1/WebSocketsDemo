<template>
    <div id="chat-window-container">
        <div id="chat-window" class="text-center container">

            <div id="chat-row-container" class="row">

                <!-- list of participants in the channel -->
                <app-participant-list></app-participant-list>

                <div class="well well-sm" v-show="showChat">
                    <small>Chatting with another user</small>
                    <div>
                        <p>chatting with {{directChatUsername}}</p>
                        <ul v-for="message in directChatMessages">
                            <li>
                                <small>{{message.author}} ({{message.time}})</small>&nbsp; {{message.text}}
                            </li>
                        </ul>
                        <input type="text" v-model="directChatInputText"/>
                        <button class="btn btn-sm btn-success" @click="sendDirectTextMessage(directChatChannel)">Send
                        </button>
                    </div>
                    <button class="btn btn-sm btn-danger" @click="()=>showDirectChat=false">End Chat</button>
                </div>

                <div class="col-md-8">
                    <!-- list of messages -->
                    <app-textmessage-list></app-textmessage-list>

                </div>
            </div>

            <!-- Text input for transcriber -->
            <app-textmessage-input></app-textmessage-input>

            <br>

            <!-- list of available channels / disconnect button if currently connected -->
            <app-channellist></app-channellist>

        </div>
    </div>
</template>

<script>

    import ChatRoomParticipantList from './ChatRoomParticipantList.vue';
    import TextMessageList from './TextMessageList.vue';
    import ChannelList from './ChannelList.vue';
    import TextMessageInput from './TextMessageInput.vue';
    import Stomp from 'stompjs';
    import config from '../config.js';
    import axios from 'axios';
    import {eventBus} from '../main.js';

    export default {

        components: {
            'app-participant-list': ChatRoomParticipantList,
            'app-textmessage-list': TextMessageList,
            'app-channellist': ChannelList,
            'app-textmessage-input': TextMessageInput
        },

        data() {
            return {
                stompClient: {},
                csrf: "",
                auth: {},
                directChannels: [],
                subscribed: [],
                directChatUsername: "",
                showDirectChat: false,
                directChatChannel: "",
                directChatInputText: "",
                directChatMessages: []
            }
        },
        methods: {

            sendDirectTextMessage() {
                this.stompClient.send("/app/direct/message/" + this.directChatChannel, {},
                    JSON.stringify({
                        'from': this.auth.name,
                        'text': this.directChatInputText,
                        'channel': {name: this.directChatChannel}
                    }));
                this.directChatInputText = "";
            },
            connect() {
                var socket = new SockJS('/shared');
                this.stompClient = Stomp.over(socket);
                eventBus.$emit('stompClient', {value: this.stompClient});
                this.stompClient.connect({'X-CSRF-TOKEN': this.csrf}, (frame) => {
                    eventBus.$emit('connected', {value: true});
                    console.log('Connected: ' + frame);

                    if (this.auth.name) {
                        this.stompClient.subscribe('/topic/direct/request/' + this.auth.name, (data) => {
                            eventBus.$emit('directChatReceive', {value: JSON.parse(data.body).from});
                            this.directChatChannel = JSON.parse(data.body).text;
                            this.stompClient.subscribe('/topic/direct/message/' + this.directChatChannel, this.handleDirectMessage);
                        });
                    }

                });
            },
            handleUnsubscribe(data) {
                const channel = data.value;
                const subscription = this.subscribed.filter(s => s.channel.channelId == channel.channelId)[0];
                for (let i = 0; i < subscription.endpoints.length; i++) {
                    subscription.endpoints[i].unsubscribe();
                }

//                for (let i = 0; i < this.subscribed.length; i++) {
//
//                    if (this.subscribed[i].channel.channelId === channel.channelId) {
//                        this.subscribed.splice(i, 1);
//                        break;
//                    }
//                }
                eventBus.$emit('updateSubscribed', {value: []});
                eventBus.$emit('clearChannelParticipants');
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
                        eventBus.$emit('showMessage', {value: data});
                    });
                    subscription.endpoints.push(sub)

                    eventBus.$emit('addSubscription', {value: subscription});
                }
            },
            disconnect() {
                if (this.stompClient != null) {
                    this.stompClient.disconnect();
                }
                eventBus.$emit('connected', {value: false});
                eventBus.$emit('updateSubscribed', {value: []});
                eventBus.$emit('clearChannelParticipants');
            },
            sendMessage(from, msg, channel, color) {
                this.stompClient.send("/app/shared/" + this.subscribed[0].channel.channelId, {},
                    JSON.stringify({'from': from, 'text': msg, 'channel': channel, 'color': color}));
            },
            handleDirectMessage(data){
                this.showDirectChat = true;
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
                this.directChatMessages.push(message);
            },
            sendDirect(username) {
                const unique = (Date.now().toString(36) + Math.random().toString(36).substr(2, 5)).toUpperCase();
                this.directChatChannel = unique;
                this.stompClient.subscribe('/topic/direct/message/' + unique, this.handleDirectMessage);

                this.stompClient.send("/app/direct/request/" + username, {},
                    JSON.stringify({
                        'from': this.auth.name,
                        'text': unique,
                        'channel': {name: username}
                    }));

                eventBus.$emit('directChatRequest', {value: username});

//                    setTimeout(() => {
//
//                        this.stompClient.send("/app/direct/message/" + unique, {},
//                            JSON.stringify({
//                                'from': this.auth.name,
//                                'text': "Hey there, " + username + ". Had a quick question?",
//                                'channel': {name: unique}
//                            }));
//                    }, 1000);
            }
        },
        computed: {
            showChat() {
                return this.showDirectChat && this.auth.name
            }
        },
        created() {

            eventBus.$on('directChatRequestInvocation', (data) => this.sendDirect(data.value));
            eventBus.$on('directChatReceive', (data) => {
                this.directChatUsername = data.value;
            });

            eventBus.$on('directChatRequest', (data) => {
                this.showDirectChat = true;
                this.directChatUsername = data.value;
            });

            eventBus.$on('addSubscription', (data) => this.subscribed.push(data.value));
            eventBus.$on('updateSubscribed', (data) => this.subscribed = data.value);
            eventBus.$on('toggleSubscription', (data) => this.toggleSubscription(data.value.channel, data.value.shouldTranscribe));
            eventBus.$on('unsubscribe', this.handleUnsubscribe);

            eventBus.$on('connect', () => this.connect());
            eventBus.$on('disconnectStomp', () => this.disconnect());
            eventBus.$on('send', (data) => this.sendMessage(data.value.from, data.value.msg, data.value.channel, data.value.color));
            eventBus.$on('auth', (data) => this.auth = data.value);

            axios.get('api/user')
                .then(res => eventBus.$emit('auth', {value: res.data}))
                .catch(err => console.log("Not logged in"));

            this.csrf = config.getCsrfHeader();
            eventBus.$emit('connect');
        }
    }
</script>

<style scoped>


</style>
