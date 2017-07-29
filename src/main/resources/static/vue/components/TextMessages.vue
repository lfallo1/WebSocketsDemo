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

            <app-textmessage-input></app-textmessage-input>

            <br>

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
            'app-participant-list' : ChatRoomParticipantList,
            'app-textmessage-list' : TextMessageList,
            'app-channellist' : ChannelList,
            'app-textmessage-input' : TextMessageInput
        },

        data() {
            return {
                connected: false,
                stompClient: {},
                csrf: "",
                auth: {},
                transcribing: false,
                channelParticipants: [],
                channels: [],
                directChannels: [],
                subscribed: []
            }
        },
        methods: {

            connect() {
                var socket = new SockJS('/shared');
                this.stompClient = Stomp.over(socket);
                eventBus.$emit('stompClient', {value: this.stompClient});
                this.stompClient.connect({'X-CSRF-TOKEN': this.csrf}, (frame) => {
                    eventBus.$emit('connected', {value: true});
                    console.log('Connected: ' + frame);

                    if (this.auth.name) {
                        this.stompClient.subscribe('/topic/direct/request/' + this.auth.name, (data) => {
                            this.directChannels.push(this.stompClient.subscribe('/topic/direct/message/' + JSON.parse(data.body).text, (data) => {
                                console.log(data);
                            }));
                        });
                    }

                });
            },
            toggleSubscription(channel, shouldTranscribe){
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
            disconnect(){
                if (this.stompClient != null) {
                    this.stompClient.disconnect();
                }
                eventBus.$emit('connected', {value: false});
                eventBus.$emit('updateSubscribed', {value: []});
                eventBus.$emit('clearChannelParticipants');
            },
            sendMessage(from, msg, channel, color){
                this.stompClient.send("/app/shared/" + this.subscribed[0].channel.channelId, {},
                    JSON.stringify({'from': from, 'text': msg, 'channel': channel, 'color': color}));
            }
        },
        created() {

            eventBus.$on('addSubscription', (data) => this.subscribed.push(data.value));
            eventBus.$on('updateSubscribed', (data) => this.subscribed = data.value);

            eventBus.$on('send', (data)=>this.sendMessage(data.value.from, data.value.msg, data.value.channel, data.value.color));
            eventBus.$on('connect', () => this.connect());
            eventBus.$on('disconnectStomp', () => this.disconnect());
            eventBus.$on('connected', (data) => this.connected = data.value);
            eventBus.$on('auth', (data) => this.auth = data.value);

            axios.get('api/user')
                .then(res => eventBus.$emit('auth', {value: res.data}))
                .catch(err => console.log("Not logged in"));

            this.csrf = config.getCsrfHeader();
            eventBus.$emit('connect');

            eventBus.$on('toggleSubscription', (data)=>{
                this.toggleSubscription(data.value.channel, data.value.shouldTranscribe)
            });
        }
    }
</script>

<style scoped>


</style>
