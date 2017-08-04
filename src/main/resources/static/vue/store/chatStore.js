import {
    CHATSTORE_ADD_CHANNEL_SUBSCRIPTION,
    CHATSTORE_CLEAR_CHANNEL_PARTICIPANTS,
    CHATSTORE_SET_CHANNEL_PARTICIPANTS,
    CHATSTORE_SET_CHANNEL_SUBSCRIPTIONS,
    CHATSTORE_SET_CHANNELS
} from './mutation-types.js';
import axios from 'axios';

export default {
    namespaced: true,
    state: {
        channels: [],
        channelSubscriptions: [],
        channelParticipants: []
    },
    getters: {
        channelsTranscribe(state) {
            return state.channels.filter(c => c.transcribers && c.transcribers.length > 0);
        },
        channelsListen(state) {
            return state.channels.filter(c => !c.transcribers || c.transcribers.length == 0);
        },
        loggedInParticipants(state) {
            return state.channelParticipants.filter(p => p.user);
        },
        hasTranscriber(state) {
            for (let i = 0; i < state.channelParticipants.length; i++) {
                if (state.channelParticipants[i].transcriber) {
                    return true;
                }
            }
            return false;
        },
        isTranscriber(state, getters, rootState) {
            if (!rootState.auth.name) {
                return false;
            }

            for (let i = 0; i < state.channelParticipants.length; i++) {
                if (state.channelParticipants[i].transcriber && state.channelParticipants[i].user.name == rootState.auth.name) {
                    return true;
                }
            }
            return false;
        },
        otherTranscriberExists(state, getters, rootState) {
            if (rootState.auth.name && getters.hasTranscriber) {
                for (let i = 0; i < state.channelParticipants.length; i++) {
                    if (state.channelParticipants[i].user && state.channelParticipants[i].user.name == rootState.auth.name
                        && state.channelParticipants[i].authenticatedToTranscribe
                        && !state.channelParticipants[i].transcriber) {
                        return true;
                    }
                }
            }
            return false;
        },
    },
    mutations: {
        [CHATSTORE_SET_CHANNELS](state, channels) {
            state.channels = channels;
        },
        [CHATSTORE_SET_CHANNEL_SUBSCRIPTIONS](state, channelSubscriptions) {
            state.channelSubscriptions = channelSubscriptions;
        },
        [CHATSTORE_ADD_CHANNEL_SUBSCRIPTION](state, channelSubscription) {
            state.channelSubscriptions.push(channelSubscription);
        },
        [CHATSTORE_CLEAR_CHANNEL_PARTICIPANTS](state) {
            state.channelParticipants = [];
        }
    },
    actions: {
        setChannelSubscriptions({commit}, channelSubscriptions) {
            commit(CHATSTORE_SET_CHANNEL_SUBSCRIPTIONS, channelSubscriptions);
        },
        addChannelSubscription({commit}, channelSubscription) {
            commit(CHATSTORE_ADD_CHANNEL_SUBSCRIPTION, channelSubscription);
        },
        unsubscribeFromChannel({state, dispatch}) {
            if (state.channelSubscriptions.length > 0) {
                for (let i = 0; i < state.channelSubscriptions[0].endpoints.length; i++) {
                    state.channelSubscriptions[0].endpoints[i].unsubscribe();
                }
                dispatch('setChannelSubscriptions', []);
                dispatch('clearChannelParticipants');
            }
        },
        setChannelParticipants({commit}, channelParticipants) {
            commit(CHATSTORE_SET_CHANNEL_PARTICIPANTS, channelParticipants);
        },
        clearChannelParticipants({commit}) {
            commit(CHATSTORE_CLEAR_CHANNEL_PARTICIPANTS);
        },
        fetchChannels({commit}) {
            axios.get('api/channel')
                .then(res => commit(CHATSTORE_SET_CHANNELS, res.data))
                .catch(err => commit(CHATSTORE_SET_CHANNELS, []));
        }
    }
};