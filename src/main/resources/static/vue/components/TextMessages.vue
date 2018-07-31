<template>
    <div id="chat-window-container">
        <div id="chat-window" class="text-center container">

            <div id="chat-row-container" class="row">

                <div class="characters-span">
                    {{ sortedCharacters }}
                </div>

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
    import {mapActions} from 'vuex';
    import {orderBy} from 'lodash';

    export default {

        data() {
            return {
                characters: [{value: 46, order: 3}, {value: 72, order: 1}, {value: 105, order: 2}]
            }
        },

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
        },
        computed: {
            sortedCharacters() {
                return orderBy(this.characters, 'order').map(c => String.fromCharCode(c.value)).join('');
            }
        }
    }
</script>

<style scoped>

</style>
