<template>
    <div>
        <div id="chat-participants-container" class="col-md-3">
            <div class="panel panel-primary" v-show="loggedInParticipants.length > 0">
                <div class="panel-heading">Logged in participants</div>
                <div class="panel-body">
                    <div :class="{'clickable': auth.name && auth.name != participant.user.name}" class="channel-participant" @click="directChatRequest(participant.user.name)"
                         v-for="participant in loggedInParticipants">
                        {{participant.user.name}}
                        <i v-if="auth.name"> {{auth.name == participant.user.name ? ' (self)' : ''}}</i>
                        <small v-if="participant.transcriber"><span class="text-primary"> [Transcriber]</span>
                        </small>
                        <hr>
                    </div>
                </div>
            </div>

            <div id="no-transcriber-warning" class="alert alert-danger text-center"
                 v-if="channelSubscriptions.length > 0 && !hasTranscriber">
                <span class="glyphicon glyphicon-info-sign"></span>&nbsp;
                There is not currently a transcriber for this channel
            </div>

            <div id="already-subscriber-warning" class="alert alert-warning text-center"
                 v-if="channelSubscriptions.length > 0 && otherTranscriberExists">
                <span class="glyphicon glyphicon-info-sign">&nbsp;</span>
                A logged in user is already transcribing on this channel. You will be able to listen, but not transcribe
            </div>
        </div>
    </div>
</template>

<script>

    import {eventBus} from '../main.js';
    import {mapState, mapActions, mapGetters} from 'vuex';

    export default {
        data() {
            return {
                stompClient: {}
            }
        },
        methods: {
            ...mapActions({
                setChannelSubscriptions: 'chat/setChannelSubscriptions',
                addChannelSubscription: 'chat/addChannelSubscription'
            }),
            directChatRequest(username) {
                eventBus.$emit('tryStartDirectChat', {value: username});
            }
        },
        computed: {
            ...mapGetters({
                loggedInParticipants: 'chat/loggedInParticipants',
                hasTranscriber: 'chat/hasTranscriber',
                otherTranscriberExists: 'chat/otherTranscriberExists'
            }),
            ...mapState({
                auth: state => state.auth,
                channelSubscriptions: state => state.chat.channelSubscriptions,
                channelParticipants: state => state.chat.channelParticipants
            })
        },
        created() {
            eventBus.$on('channelParticipants', (data) => this.channelParticipants = data.value);
            eventBus.$on('clearChannelParticipants', (data) => this.channelParticipants = []);
        }
    }

</script>

<style scoped>
    #chat-row-container .channel-participant {
        margin-top: -10px;
    }

    #chat-row-container .channel-participant hr {
        margin-top: 5px;
    }
</style>