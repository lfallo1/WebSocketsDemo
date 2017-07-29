<template>
    <div>
        <div v-if="subscribed.length == 0">
            <div id="subscription-list" v-if="connected">
                <h3>Listen to feed</h3>
                <div class="btn-group text-center" role="group">
                    <button :class="{'active' : subscribed.filter(s=>s.channel == channel).length > 0}"
                            class="btn btn-default"
                            @click="toggleSubscription(channel)" v-for="channel in channelsListen">{{channel.name}}
                    </button>
                </div>
            </div>

            <div id="transcribe-list" v-if="auth.name && connected">
                <h3>Transcribe to feed</h3>
                <div class="btn-group text-center" role="group">
                    <button :class="{'active' : subscribed.filter(s=>s.channel == channel).length > 0}"
                            class="btn btn-default"
                            @click="toggleSubscription(channel)" v-for="channel in channelsTranscribe">
                        {{channel.name}}
                    </button>
                </div>
            </div>
        </div>

        <div v-else>
            <button class="btn btn-danger" @click="unsubscribe">Stop listening / transcribing</button>
        </div>
    </div>
</template>

<script>

    import {eventBus} from '../main.js';
    import config from '../config.js';
    import axios from 'axios';

    export default{
        data(){
            return {
                auth: {},
                connected: false,
                subscribed: [],
                channels: []
            }
        },
        computed:{
            channelsListen() {
                return this.channels.filter(c => !c.transcribers || c.transcribers.length == 0);
            },
            channelsTranscribe() {
                return this.channels.filter(c => c.transcribers && c.transcribers.length > 0);
            }
        },
        methods:{
            toggleSubscription (channel, shouldTranscribe) {
                eventBus.$emit('toggleSubscription', {value: {channel: channel, shouldTranscribe: shouldTranscribe}})
            },
            unsubscribe() {
                if (this.subscribed.length > 0) {
                    const currentSubscription = this.subscribed[0];
                    eventBus.$emit('unsubscribe', {value: currentSubscription.channel});
                }
            },
            handleUnsubscribe(data) {
                const channel = data.value;
                const subscription = this.subscribed.filter(s => s.channel.channelId == channel.channelId)[0];
                for (let i = 0; i < subscription.endpoints.length; i++) {
                    subscription.endpoints[i].unsubscribe();
                }

                for (let i = 0; i < this.subscribed.length; i++) {

                    if (this.subscribed[i].channel.channelId === channel.channelId) {
                        this.subscribed.splice(i, 1);
                        break;
                    }
                }
                eventBus.$emit('updateSubscribed', {value: []});
                eventBus.$emit('clearChannelParticipants');
            },
            disconnect() {
                eventBus.$emit('disconnectStomp');
            }
        },
        created(){
            eventBus.$on('connected', (data)=>this.connected = data.value);
            eventBus.$on('unsubscribe', this.handleUnsubscribe);
            eventBus.$on('addSubscription', (data) => this.subscribed.push(data.value));
            eventBus.$on('updateSubscribed', (data) => this.subscribed = data.value);
            eventBus.$on('auth', (data) => this.auth = data.value);
            eventBus.$on('channels', (data) => {
                this.channels = data.value
            });
            eventBus.$on('disconnect', () => this.disconnect());

            eventBus.$on('channelParticipants', (data) => {
                this.channelParticipants = data.value
            });
            eventBus.$on('clearChannelParticipants', (data) => this.channelParticipants = []);

            this.csrf = config.getCsrfHeader();

            axios.get('api/channel')
                .then(res => {
                    eventBus.$emit('channels', {value: res.data})
                })
                .catch(err => console.log("Error loading channels"));
        }
    }

</script>

<style scoped>
    #subscription-list .btn, #transcribe-list .btn {
        color: #428bca;
    }

    #subscription-list h3, #transcribe-list h3 {
        color: #428bca;
        font-size: 20px;
        font-weight: bold;
    }
</style>