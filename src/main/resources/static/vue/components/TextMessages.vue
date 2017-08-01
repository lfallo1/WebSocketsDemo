<template>
    <div id="chat-window-container">
        <div id="chat-window" class="text-center container">

            <div id="chat-row-container" class="row">

                <!-- list of participants in the channel -->
                <app-participant-list></app-participant-list>

                <app-directchatsessions></app-directchatsessions>

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
    import DirectChatSessions from './DirectChatSessions.vue';
    import Stomp from 'stompjs';
    import config from '../config.js';
    import axios from 'axios';
    import {eventBus} from '../main.js';

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
                csrf: "",
                auth: {},
                subscribed: []
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
                eventBus.$emit('stompClient', {value: this.stompClient});
                this.stompClient.connect({'X-CSRF-TOKEN': this.csrf}, (frame) => {
                    eventBus.$emit('connected', {value: true});
                    console.log('Connected: ' + frame);

                    if (this.auth.name) {
                        this.stompClient.subscribe('/topic/direct/request/' + this.auth.name, (data) => {
                            const channel = JSON.parse(data.body).text;
                            eventBus.$emit('directChatRequest', {value: {username: JSON.parse(data.body).from, channel: channel}});
                            this.stompClient.subscribe('/topic/direct/message/' + channel, (data)=>eventBus.$emit('handleDirectMessage', data));
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
            directChatStart(username) {
                const unique = (Date.now().toString(36) + Math.random().toString(36).substr(2, 5)).toUpperCase();
                this.directChatChannel = unique;
                this.stompClient.subscribe('/topic/direct/message/' + unique, (data)=>eventBus.$emit('handleDirectMessage', data));

                this.stompClient.send("/app/direct/request/" + username, {},
                    JSON.stringify({
                        'from': this.auth.name,
                        'text': unique,
                        'channel': {name: username}
                    }));

                eventBus.$emit('directChatRequest', {value: {username: username, channel: unique}});
            }
        },
        created() {

            // direct channel event listeners
            eventBus.$on('sendDirectTextMessage', (data)=>this.sendDirectTextMessage(data.value.text, data.value.channel));
            eventBus.$on('directChatRequestInvocation', (data) => this.directChatStart(data.value));


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
