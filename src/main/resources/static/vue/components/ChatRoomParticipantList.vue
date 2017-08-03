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
                 v-if="subscribed.length > 0 && !hasTranscriber">
                <span class="glyphicon glyphicon-info-sign"></span>&nbsp;
                There is not currently a transcriber for this channel
            </div>

            <div id="already-subscriber-warning" class="alert alert-warning text-center"
                 v-if="subscribed.length > 0 && otherTranscriberExists">
                <span class="glyphicon glyphicon-info-sign">&nbsp;</span>
                A logged in user is already transcribing on this channel. You will be able to listen, but not transcribe
            </div>
        </div>
    </div>
</template>

<script>

    import {eventBus} from '../main.js';
    import {mapState} from 'vuex';

    export default {
        data() {
            return {
                subscribed: [],
                channelParticipants: [],
                stompClient: {}
            }
        },
        methods: {
            directChatRequest(username) {
                eventBus.$emit('tryStartDirectChat', {value: username});
            }
        },
        computed: {
            loggedInParticipants() {
                return this.channelParticipants.filter(p => p.user);
            },
            hasTranscriber() {
                for (let i = 0; i < this.channelParticipants.length; i++) {
                    if (this.channelParticipants[i].transcriber) {
                        return true;
                    }
                }
                return false;
            },
            otherTranscriberExists() {
                if (this.auth.name && this.hasTranscriber) {
                    for (let i = 0; i < this.channelParticipants.length; i++) {
                        if (this.channelParticipants[i].user && this.channelParticipants[i].user.name == this.auth.name
                            && this.channelParticipants[i].authenticatedToTranscribe
                            && !this.channelParticipants[i].transcriber) {
                            return true;
                        }
                    }
                }
                return false;
            },
            ...mapState({
                auth: state => state.authStore.auth
            })
        },
        created() {
            eventBus.$on('addSubscription', (data) => this.subscribed.push(data.value));
            eventBus.$on('updateSubscribed', (data) => this.subscribed = data.value);
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