<template>
    <div>
        <div id="chat-participants-container" class="col-md-3">
            <div class="panel panel-primary" v-show="loggedInParticipants.length > 0">
                <div class="panel-heading">Channel participants</div>
                <div class="panel-body">
                    <div class="channel-participant"
                         v-for="participant in loggedInParticipants">
                        {{participant.user.name}}
                        <i v-if="auth.name">{{auth.name == participant.user.name ? ' (self)' : ''}}</i>
                        <small v-if="participant.transcriber"><span
                                class="text-primary glyphicon glyphicon-user"> [Transcriber]</span>
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

    export default {
        data() {
            return {
                subscribed: [],
                channelParticipants: [],
                auth: {},
                stompClient: {}
            }
        },
        methods: {
            sendDirect(username) {

//                if (this.auth.name && username != this.auth.name) {
//
//                    const unique = (Date.now().toString(36) + Math.random().toString(36).substr(2, 5)).toUpperCase();
//
//                    this.directChannels.push(this.stompClient.subscribe('/topic/direct/message/' + unique, (data) => {
//                        console.log(data);
//                    }));
//
//                    this.stompClient.send("/app/direct/request/" + username, {},
//                        JSON.stringify({
//                            'from': this.auth.name,
//                            'text': unique,
//                            'channel': {name: username}
//                        }));
//
//                    setTimeout(() => {
//
//                        this.stompClient.send("/app/direct/message/" + unique, {},
//                            JSON.stringify({
//                                'from': this.auth.name,
//                                'text': "Hey there, " + username + ". Had a quick question?",
//                                'channel': {name: unique}
//                            }));
//                    }, 1000);
//                }
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
            }
        },
        created() {
            eventBus.$on('updateSubscribed', (data) => this.subscribed = data.value);
            eventBus.$on('auth', (data) => this.auth = data.value);
            eventBus.$on('channelParticipants', (data) => {
                this.channelParticipants = data.value
            });
            eventBus.$on('clearChannelParticipants', (data) => this.channelParticipants = []);
            eventBus.$on('addSubscription', (data) => this.subscribed.push(data.value));
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