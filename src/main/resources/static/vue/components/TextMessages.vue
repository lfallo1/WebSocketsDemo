<template>
    <div id="chat-window-container">
        <div id="chat-window" class="text-center container">

            <div id="chat-row-container" class="row">

                <!-- list of participants in the channel -->
                <app-participant-list></app-participant-list>


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

            <div id="direct-chat-windows">
                <app-directchatsessions></app-directchatsessions>
            </div>

        </div>
    </div>
</template>

<script>

    import ChatRoomParticipantList from './ChatRoomParticipantList.vue';
    import TextMessageList from './TextMessageList.vue';
    import ChannelList from './ChannelList.vue';
    import TextMessageInput from './TextMessageInput.vue';
    import DirectChatSessions from './DirectChatSessions.vue';
    import Stomp from 'stompjs';
    import config from '../config.js';
    import axios from 'axios';
    import {eventBus} from '../main.js';
    import {mapState, mapActions} from 'vuex';

    export default {

        components: {
            'app-participant-list': ChatRoomParticipantList,
            'app-textmessage-list': TextMessageList,
            'app-channellist': ChannelList,
            'app-textmessage-input': TextMessageInput,
            'app-directchatsessions': DirectChatSessions
        },

        data() {
            return {
                stompClient: {},
                csrf: ""
            }
        },
        methods: {

            sendDirectTextMessage(directChatInputText, directChatChannel) {
                this.stompClient.send("/app/direct/message/" + directChatChannel, {},
                    JSON.stringify({
                        'from': this.auth.name,
                        'text': directChatInputText,
                        'channel': {name: directChatChannel}
                    }));
            },
            connect() {
                var socket = new SockJS('/shared');
                this.stompClient = Stomp.over(socket);
                this.stompClient.connect({'X-CSRF-TOKEN': this.csrf}, (frame) => {

                    //get notified whenever user connects or disconnects
                    this.stompClient.subscribe('/topic/users/connected', (data) => eventBus.$emit('usersConnected', {value: data}));
                    this.stompClient.subscribe('/topic/users/disconnect', (data) => eventBus.$emit('disconnectUser', {value: data}));

                    eventBus.$emit('connected', {value: true});
                    console.log('Connected: ' + frame);

                    if (this.auth.name) {

                        this.stompClient.subscribe('/topic/direct/request/' + this.auth.name, (data) => {

                            //MOVE TO STORE
                            const channel = JSON.parse(data.body).text;
                            const username = JSON.parse(data.body).from;
                            this.addDirectChatSession({
                                directChatUsernames: [username, this.auth.name],
                                directChatChannel: channel,
                                directChatMessages: [],
                                directChatInputText: "",
                                showDirectChat: false,
                                visible: true,
                                isHidden: false
                            });

                            //subscribe to direct message and store subscription
                            let directMessageSubscription = this.stompClient.subscribe('/topic/direct/message/' + channel, (data) => this.handleDirectMessage(data));
                            directMessageSubscription.users = [JSON.parse(data.body).from, this.auth.name];
                            this.addDirectMessageSubscription(directMessageSubscription);
                        });
                    }

                });
            },
            toggleSubscription(channel, shouldTranscribe) {
                if (this.stompClient.subscribe) {

                    //i don't think its a good idea to allow multiple sessions simultaneously.
                    if (this.channelSubscriptions.length > 0) {
                        this.unsubscribeFromChannel(this.channelSubscriptions[0].channel);
                    }

                    let subscription = {
                        endpoints: [],
                        channel: channel
                    };

                    let sub = this.stompClient.subscribe('/topic/channelcount/' + channel.channelId, (data) => {
                        this.setChannelParticipants(JSON.parse(data.body));
                    });
                    subscription.endpoints.push(sub)

                    sub = this.stompClient.subscribe('/topic/transcription/' + channel.channelId, (data) => {
                        eventBus.$emit('showMessage', {value: data});
                    });
                    subscription.endpoints.push(sub)

                    this.addChannelSubscription(subscription);
                }
            },
            disconnect() {
                if (this.stompClient != null) {
                    this.stompClient.disconnect();
                }
                eventBus.$emit('connected', {value: false});
                this.setChannelSubscriptions([]);
                this.clearChannelParticipants();
            },
            sendMessage(from, msg, channel, color) {
                this.stompClient.send("/app/shared/" + this.channelSubscriptions[0].channel.channelId, {},
                    JSON.stringify({'from': from, 'text': msg, 'channel': channel, 'color': color}));
            },
            directChatStart(username) {

                // MOVE TO STORE

                const unique = (Date.now().toString(36) + Math.random().toString(36).substr(2, 5)).toUpperCase();
                this.directChatChannel = unique;

                //subscribe to direct message and store subscription
                let directMessageSubscription = this.stompClient.subscribe('/topic/direct/message/' + unique, (data) => this.handleDirectMessage(data));
                directMessageSubscription.users = [username, this.auth.name];
                this.addDirectMessageSubscription(directMessageSubscription);

                this.stompClient.send("/app/direct/request/" + username, {},
                    JSON.stringify({
                        'from': this.auth.name,
                        'text': unique,
                        'channel': {name: username}
                    }));

                this.addDirectChatSession({
                    directChatUsernames: [username, this.auth.name],
                    directChatChannel: unique,
                    directChatMessages: [],
                    directChatInputText: "",
                    showDirectChat: false,
                    visible: true,
                    isHidden: false
                });

            },
            ...mapActions({
                fetchUser: 'fetchUser',
                setChannelParticipants: 'chat/setChannelParticipants',
                setChannelSubscriptions: 'chat/setChannelSubscriptions',
                clearChannelParticipants: 'chat/clearChannelParticipants',
                addChannelSubscription: 'chat/addChannelSubscription',
                addDirectMessageSubscription: 'chat/addDirectMessageSubscription',
                disconnectUser: 'chat/disconnectUser',
                handleDirectMessage: 'chat/handleDirectMessage',
                addDirectChatSession: 'chat/addDirectChatSession'
            })
        },
        computed: {
            ...mapState({
                auth: state => state.auth,
                channelSubscriptions: state => state.chat.channelSubscriptions,
                channelParticipants: state => state.chat.channelParticipants
            })
        },
        created() {

            // direct channel event listeners
            eventBus.$on('sendDirectTextMessage', (data) => this.sendDirectTextMessage(data.value.text, data.value.channel));
            eventBus.$on('directChatRequestInvocation', (data) => this.directChatStart(data.value));

            eventBus.$on('toggleSubscription', (data) => this.toggleSubscription(data.value.channel));

            eventBus.$on('disconnectStomp', () => this.disconnect());
            eventBus.$on('send', (data) => this.sendMessage(data.value.from, data.value.msg, data.value.channel, data.value.color));

            this.fetchUser();

            this.csrf = config.getCsrfHeader();
            this.connect()
        }
    }
</script>

<style scoped>


</style>
