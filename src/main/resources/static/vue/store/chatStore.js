import {
    CHATSTORE_ADD_CHANNEL_SUBSCRIPTION,
    CHATSTORE_CLEAR_CHANNEL_PARTICIPANTS,
    CHATSTORE_SET_CHANNEL_PARTICIPANTS,
    CHATSTORE_SET_CHANNEL_SUBSCRIPTIONS,
    CHATSTORE_SET_CHANNELS,
    CHATSTORE_ADD_DIRECT_MESSAGE_SUBSCRIPTION,
    CHATSTORE_UNSUBSCRIBE_DIRECT_MESSAGE_SUBSCRIPTION_BY_USER,
    CHATSTORE_REMOVE_DIRECT_CHAT_SESSION_BY_USER,
    CHATSTORE_ADD_DIRECT_MESSAGE,
    CHATSTORE_ADD_DIRECT_CHAT_SESSION,
    CHATSTORE_SET_DIRECT_CHAT_SESSIONS,
    CHATSTORE_SET_CONNECTED
} from './mutation-types.js';
import axios from 'axios';
import config from '../config.js';

export default {
    namespaced: true,
    state: {
        channels: [],
        channelSubscriptions: [],
        channelParticipants: [],
        directMessageSubscriptions: [],
        directChatSessions: []
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
        hasExistingChat: (state) => (username) => {
            for (let i = 0; i < state.directChatSessions.length; i++) {
                for (let j = 0; j < state.directChatSessions[i].directChatUsernames.length; j++) {
                    if (state.directChatSessions[i].directChatUsernames[j] == username) {
                        return state.directChatSessions[i];
                    }
                }
            }
            return undefined;
        },
        subscribeText(state){
            //they will have multiple subscriptions on same channel. so get unique list for display purposes
            const arr = config.unique(state.channelSubscriptions);
            return state.channelSubscriptions.length > 0 ? '(Listening on ' + arr.map(s => s.channel.name).toString() + ' - ' + state.channelParticipants.length + ' total) ' : '';
        }
    },
    mutations: {
        [CHATSTORE_SET_CONNECTED](state, value) {
            state.connected = value;
        },
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
        },
        [CHATSTORE_SET_CHANNEL_PARTICIPANTS](state, channelParticipants) {
            state.channelParticipants = channelParticipants;
        },
        [CHATSTORE_ADD_DIRECT_MESSAGE_SUBSCRIPTION](state, subscription){
            state.directMessageSubscriptions.push(subscription);
        },
        [CHATSTORE_UNSUBSCRIBE_DIRECT_MESSAGE_SUBSCRIPTION_BY_USER](state, user){
            for (let i = 0; i < state.directMessageSubscriptions.length; i++) {
                if (state.directMessageSubscriptions[i].users.indexOf(user) > -1) {
                    state.directMessageSubscriptions[i].unsubscribe();
                }
            }
        },
        [CHATSTORE_REMOVE_DIRECT_CHAT_SESSION_BY_USER](state, user){
            for (let i = 0; i < state.directChatSessions.length; i++) {
                if (state.directChatSessions[i].directChatUsernames.indexOf(user) > -1) {
                    state.directChatSessions.splice(i, 1);
                    break;
                }
            }
        },
        [CHATSTORE_ADD_DIRECT_CHAT_SESSION](state, session){
            state.directChatSessions.push(session);
        },
        [CHATSTORE_SET_DIRECT_CHAT_SESSIONS](state, sessions){
            state.directChatSessions = sessions;
        },
        [CHATSTORE_ADD_DIRECT_MESSAGE](state, message){
            let session = state.directChatSessions.filter(s => s.directChatChannel == message.channel.name)[0];
            session.directChatMessages.push(message);
            session.isHidden = false;
        }
    },
    actions: {
        setConnected({commit}, isConnected){
            commit(CHATSTORE_SET_CONNECTED, isConnected);
        },
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
        },
        addDirectMessageSubscription({commit}, subscription){
            commit(CHATSTORE_ADD_DIRECT_MESSAGE_SUBSCRIPTION, subscription);
        },
        addDirectChatSession({commit}, session){
            commit(CHATSTORE_ADD_DIRECT_CHAT_SESSION, session);
        },
        setDirectChatSessions({commit}, sessions){
            commit(CHATSTORE_SET_DIRECT_CHAT_SESSIONS, sessions);
        },
        disconnectUser({commit}, user){
            commit(CHATSTORE_UNSUBSCRIBE_DIRECT_MESSAGE_SUBSCRIPTION_BY_USER, user);
            commit(CHATSTORE_REMOVE_DIRECT_CHAT_SESSION_BY_USER, user);
        },
        handleDirectMessage({commit}, data){
            let author = JSON.parse(data.body).from;
            let text = JSON.parse(data.body).text;
            let channel = JSON.parse(data.body).channel;
            let time = JSON.parse(data.body).time;
            const message = {
                'author': author,
                'text': text,
                'channel': channel,
                'time': time
            };

            commit(CHATSTORE_ADD_DIRECT_MESSAGE, message);
        }
    }
};