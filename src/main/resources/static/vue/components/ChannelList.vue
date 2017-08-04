<template>
    <div>
        <div v-if="channelSubscriptions.length == 0">
            <div id="subscription-list" v-if="connected">
                <h3>Listen to feed</h3>
                <div class="btn-group text-center" role="group">
                    <button :class="{'active' : channelSubscriptions.filter(s=>s.channel == channel).length > 0}"
                            class="btn btn-default"
                            @click="toggleSubscription(channel)" v-for="channel in channelsListen">{{channel.name}}
                    </button>
                </div>
            </div>

            <div id="transcribe-list" v-if="auth.name && connected">
                <h3>Transcribe to feed</h3>
                <div class="btn-group text-center" role="group">
                    <button :class="{'active' : channelSubscriptions.filter(s=>s.channel == channel).length > 0}"
                            class="btn btn-default"
                            @click="toggleSubscription(channel)" v-for="channel in channelsTranscribe">
                        {{channel.name}}
                    </button>
                </div>
            </div>
        </div>

        <div v-else>
            <button class="btn btn-danger" @click="unsubscribeFromChannel">Stop listening / transcribing</button>
        </div>
    </div>
</template>

<script>

    import {eventBus} from '../main.js';
    import config from '../config.js';
    import {mapState, mapActions, mapGetters} from 'vuex';

    export default {
        data() {
            return {
                connected: false,
            }
        },
        computed: {
            ...mapGetters({
                channelsListen: 'chat/channelsListen',
                channelsTranscribe: 'chat/channelsTranscribe'
            }),
            ...mapState({
                auth: state => state.auth,
                channelSubscriptions: state => state.chat.channelSubscriptions,
                channels: state => state.chat.channels
            })
        },
        methods: {
            ...mapActions({
                unsubscribeFromChannel: 'chat/unsubscribeFromChannel',
                fetchChannels: 'chat/fetchChannels'
            }),
            toggleSubscription(channel) {
                eventBus.$emit('toggleSubscription', {value: {channel: channel}})
            },
            disconnect() {
                eventBus.$emit('disconnectStomp');
            }
        },
        created() {
            this.fetchChannels();
            this.csrf = config.getCsrfHeader();
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