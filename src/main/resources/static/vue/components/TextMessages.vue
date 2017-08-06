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
            'app-directchatsessions': DirectChatSessions,
        },
        methods: {
            ...mapActions({
                setCsrf: 'setCsrf',
                connect: 'chat/connect',
                fetchUser: 'fetchUser',
            })
        },
        created() {
            this.setCsrf();
            this.fetchUser();
            this.connect();
        }
    }
</script>

<style scoped>


</style>
