<template>
    <div>
        <div v-if="channelSubscriptions.length == 0">
            <div v-if="channelsListen.length === 0 && channelsTranscribe.length === 0">
                <div class="text-danger">There are currently channels available</div>
            </div>
            <div id="subscription-list" v-if="connected && channelsListen.length > 0">
                <h3><i class="material-icons">rss_feed</i>&nbsp;Listen to feed</h3>
                <div class="btn-group text-center" role="group">
                    <button :class="{'active' : channelSubscriptions.filter(s=>s.channel == channel).length > 0}"
                            class="btn btn-default"
                            @click="subscribeToChannel(channel)" v-for="channel in channelsListen">{{channel.name}}
                    </button>
                </div>
            </div>

            <div id="transcribe-list" v-if="auth.name && connected && channelsTranscribe.length > 0">
                <h3><i class="material-icons">mode_edit</i>&nbsp;Transcribe to feed</h3>
                <div class="btn-group text-center" role="group">
                    <button :class="{'active' : channelSubscriptions.filter(s=>s.channel == channel).length > 0}"
                            class="btn btn-default"
                            @click="subscribeToChannel(channel)" v-for="channel in channelsTranscribe">
                        {{channel.name}}
                    </button>
                </div>
            </div>
        </div>

        <div v-else>
            <button class="btn btn-danger" @click="unsubscribeFromChannel">Stop listening / transcribing</button>
            <div class="subscribed-text text-primary">
                <i class="material-icons with-text md-18">rss_feed</i>
                {{subscribedText}}
            </div>
        </div>
    </div>
</template>

<script>

    import {mapState, mapActions, mapGetters} from 'vuex';

    export default {
        computed: {
            ...mapGetters({
                channelsListen: 'chat/channelsListen',
                channelsTranscribe: 'chat/channelsTranscribe',
                subscribedText: 'chat/subscribedText'
            }),
            ...mapState({
                auth: state => state.auth,
                channelSubscriptions: state => state.chat.channelSubscriptions,
                channels: state => state.chat.channels,
                connected: state => state.chat.connected
            })
        },
        methods: {
            ...mapActions({
                unsubscribeFromChannel: 'chat/unsubscribeFromChannel',
                subscribeToChannel: 'chat/subscribeToChannel',
                disconnect: 'chat/disconnect',
                fetchChannels: 'chat/fetchChannels'
            })
        },
        created(){
            this.fetchChannels();
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

    .subscribed-text{
        margin-top: 7px;
        font-size: 13px;
        font-weight: bold;
    }
</style>