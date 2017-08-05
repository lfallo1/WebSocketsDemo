import Vue from "vue";
import {
    CHATSTORE_STOMP_CLIENT_CONNECT,
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
    CHATSTORE_SET_CONNECTED,
    CHATSTORE_TOGGLE_COLOR,
    CHATSTORE_ADD_MESSAGE,
    CHATSTORE_SET_USERS_CONNECTED,
    CHATSTORE_UPDATE_CURRENT_MESSAGE,
    CHATSTORE_CLOSE_DIRECT_CHAT_SESSION,
    CHATSTORE_SET_CURRENT_LINE,
    CHATSTORE_UPDATE_DIRECTCHATSESSION_TEXT
} from './mutation-types.js';
import axios from 'axios';
import config from '../../config.js';
import {eventBus} from '../../main.js';

export default {
    namespaced: true,
    state: {
        stompClient: {},
        channels: [],
        channelSubscriptions: [],
        channelParticipants: [],
        directMessageSubscriptions: [],
        directChatSessions: [],
        usersConnected: [],
        connected: false,
        color: false,
        messages: [],
        currentLine: {value: "", channel: "", author: "", time: undefined},
        currentMessage: ""
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
        subscribedText(state){
            //they will have multiple subscriptions on same channel. so get unique list for display purposes
            const arr = config.unique(state.channelSubscriptions);
            return state.channelSubscriptions.length > 0 ? '(Listening on ' + arr.map(s => s.channel.name).toString() + ' - ' + state.channelParticipants.length + ' total) ' : '';
        },
        textColor(state) {
            return state.color ? 'info' : 'warning'
        }
    },
    mutations: {
        [CHATSTORE_STOMP_CLIENT_CONNECT](state, socket){
            state.stompClient = Stomp.over(socket);
        },
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
        [CHATSTORE_SET_USERS_CONNECTED](state, usersConnected){
            state.usersConnected = usersConnected;
        },

        [CHATSTORE_TOGGLE_COLOR](state){
            state.color = !state.color;
        },
        [CHATSTORE_ADD_MESSAGE](state, payload){
            state.messages.push({data: payload.data, textClass: payload.textClass});
        },
        [CHATSTORE_UPDATE_CURRENT_MESSAGE](state, currentMessage){
            state.currentMessage = currentMessage;
        },
        [CHATSTORE_SET_CURRENT_LINE](state, currentLine){
            state.currentLine = currentLine;
        },

        [CHATSTORE_CLOSE_DIRECT_CHAT_SESSION](state, session){
            for(let i = 0; i < state.directChatSessions.length; i++){
                if(state.directChatSessions[i] == session){
                    state.directChatSessions[i].isHidden = true;
                    break;
                }
            }
            // session.isHidden = true;
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
                    Vue.toasted.error(user + ' is disconnected. Chat session closed.', {
                        position: 'bottom-right',
                        icon: 'block'
                    }).goAway(3500);
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
        },
        [CHATSTORE_ADD_DIRECT_MESSAGE_SUBSCRIPTION](state, subscription){
            state.directMessageSubscriptions.push(subscription);
        },
        [CHATSTORE_UPDATE_DIRECTCHATSESSION_TEXT](state, payload){
            for(let i = 0; i < state.directChatSessions.length; i++){
                if(state.directChatSessions[i] == payload.session){
                    state.directChatSessions[i].directChatInputText = payload.text;
                    break;
                }
            }
        }
    },
    actions: {
        connect({commit, dispatch, state, rootState}){

            var socket = new SockJS('/shared');
            commit(CHATSTORE_STOMP_CLIENT_CONNECT, socket);

            state.stompClient.connect({'X-CSRF-TOKEN': rootState.csrf}, (frame) => {

                Vue.toasted.success('<span class="glyphicon glyphicon-success"></span>&nbsp; You are connected!', {
                    position: 'bottom-right',
                    'full-width': true,
                    icon : 'rss_feed'
                }).goAway(3500);

                commit(CHATSTORE_SET_DIRECT_CHAT_SESSIONS, []);

                //get notified whenever user connects or disconnects
                state.stompClient.subscribe('/topic/users/connected', (data) => dispatch('setUsersConnected',data));
                state.stompClient.subscribe('/topic/users/disconnect', (data) => dispatch('disconnectUser', data));

                commit(CHATSTORE_SET_CONNECTED, true);

                if (rootState.auth.name) {

                    state.stompClient.subscribe('/topic/direct/request/' + rootState.auth.name, (data) => {
                        dispatch('addDirectChatSession',{username: JSON.parse(data.body).from, channel: JSON.parse(data.body).text});

                        //subscribe to direct message and store subscription
                        let directMessageSubscription = state.stompClient.subscribe('/topic/direct/message/' + JSON.parse(data.body).text, (data) => dispatch('handleDirectMessage', data));
                        directMessageSubscription.users = [JSON.parse(data.body).from, rootState.auth.name];
                        dispatch('addDirectMessageSubscription', directMessageSubscription);
                    });
                }

            });
        },
        disconnect({dispatch, commit, state}) {
            if (state.stompClient != null) {
                state.stompClient.disconnect();
                Vue.toasted.error('Disconnected from server', {
                    position: 'bottom-right',
                    icon: 'do_not_disturb'
                }).goAway(3500);
            }
            commit(CHATSTORE_SET_CONNECTED, false);
            dispatch('setChannelSubscriptions',[]);
            dispatch('clearChannelParticipants');
            commit(CHATSTORE_SET_DIRECT_CHAT_SESSIONS, []);
        },
        setConnected({commit}, isConnected){
            commit(CHATSTORE_SET_CONNECTED, isConnected);
        },
        setUsersConnected({commit}, data){
            commit(CHATSTORE_SET_USERS_CONNECTED, JSON.parse(data.body));
        },
        disconnectUser({commit}, payload){
            const user = payload.body;
            commit(CHATSTORE_UNSUBSCRIBE_DIRECT_MESSAGE_SUBSCRIPTION_BY_USER, user);
            commit(CHATSTORE_REMOVE_DIRECT_CHAT_SESSION_BY_USER, user);
            //TODO show toaster
        },

        updateCurrentMessage({commit}, currentMessage){
            commit(CHATSTORE_UPDATE_CURRENT_MESSAGE, currentMessage);
        },
        toggleColor({commit}){
            commit(CHATSTORE_TOGGLE_COLOR);
        },
        sendMessage({state}, payload) {
            state.stompClient.send("/app/shared/" + state.channelSubscriptions[0].channel.channelId, {},
                JSON.stringify({'from': payload.from, 'text': payload.msg, 'channel': payload.channel, 'color': payload.color}));
        },
        showMessage({state, dispatch}, data) {
            let author = JSON.parse(data.body).from;
            let value = JSON.parse(data.body).text;
            let color = JSON.parse(data.body).color;
            let channel = JSON.parse(data.body).channel;
            let time = JSON.parse(data.body).time;
            console.log("received message from channel: " + channel.toString());

            if (value.toLowerCase() == 'enter') {
                if (state.currentLine.value) {
                    dispatch('setCurrentLine', {value: state.currentLine.value, author: channel.name, time: time, author: author});
                    dispatch('addMessage',{data: state.currentLine, textClass: color});
                    dispatch('setCurrentLine', {value: "", channel: "", author: "", time: undefined});
                    dispatch('updateCurrentMessage', "");
                }
                dispatch('toggleColor');
            }
            else if (value.toLowerCase() == 'backspace') {
                if (state.currentLine.value.length > 0) {
                    const value = state.currentLine.value.substring(0, state.currentLine.value.length - 1);
                    dispatch('setCurrentLine', {value: value, color: color});
                }
            } else {
                dispatch('setCurrentLine', {color: color, value: state.currentLine.value + value});
            }

        },
        addMessage({state, commit}, payload){
            const res = commit(CHATSTORE_ADD_MESSAGE, payload);
            setTimeout(()=>{
                eventBus.$emit('transcribeScroll');
            },100)
        },
        setCurrentLine({commit}, payload){
            commit('chat/setCurrentLine', {value: payload.value, color: payload.color, time: payload.time, author: payload.author});
        },

        subscribeToChannel({dispatch, state}, channel) {
            if (state.stompClient.subscribe) {

                //i don't think its a good idea to allow multiple sessions simultaneously.
                if (state.channelSubscriptions.length > 0) {
                    state.unsubscribeFromChannel(state.channelSubscriptions[0].channel);
                }

                let subscription = {
                    endpoints: [],
                    channel: channel
                };

                let sub = state.stompClient.subscribe('/topic/channelcount/' + channel.channelId, (data) => {
                    dispatch('setChannelParticipants', JSON.parse(data.body));
                });
                subscription.endpoints.push(sub)

                sub = state.stompClient.subscribe('/topic/transcription/' + channel.channelId, (data) => {
                    dispatch('showMessage', data);
                });
                subscription.endpoints.push(sub)

                dispatch('addChannelSubscription', subscription);

                Vue.toasted.success('subscribed to ' + channel.name, {
                    position: 'bottom-right',
                    icon: 'block'
                }).goAway(3500);
            }
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
                    Vue.toasted.warning('unsubscribed from ' + state.channelSubscriptions[0].channel.name, {
                        position: 'bottom-right',
                        icon: 'block'
                    }).goAway(3500);
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

        directChatRequest({commit, dispatch, state, getters, rootState}, username) {
            //TODO modifying state directly
            const session = getters.hasExistingChat(username);
            if (session) {
                session.isHidden = false;
            }
            else if (rootState.auth.name && username != rootState.auth.name) {
                dispatch('directChatStart', username);
            }
        },
        directChatStart({commit, state, dispatch, rootState} , username) {
            const unique = (Date.now().toString(36) + Math.random().toString(36).substr(2, 5)).toUpperCase();

            //subscribe to direct message and store subscription
            let directMessageSubscription = state.stompClient.subscribe('/topic/direct/message/' + unique, (data) => dispatch('handleDirectMessage', data));
            directMessageSubscription.users = [username, rootState.auth.name];
            dispatch('addDirectMessageSubscription', directMessageSubscription);

            state.stompClient.send("/app/direct/request/" + username, {},
                JSON.stringify({
                    'from': rootState.auth.name,
                    'text': unique,
                    'channel': {name: username}
                }));

            dispatch('addDirectChatSession',{channel: unique, username: username});

        },
        addDirectMessageSubscription({commit}, subscription){
            commit(CHATSTORE_ADD_DIRECT_MESSAGE_SUBSCRIPTION, subscription);
        },
        addDirectChatSession({commit, rootState}, payload){
            const session = {
                directChatUsernames: [payload.username, rootState.auth.name],
                directChatChannel: payload.channel,
                directChatMessages: [],
                directChatInputText: "",
                showDirectChat: false,
                visible: true,
                isHidden: false
            }
            commit(CHATSTORE_ADD_DIRECT_CHAT_SESSION, session);

            Vue.toasted.info('New chat session with ' + payload.username, {
                position: 'bottom-left',
                icon: 'chat'
            }).goAway(3500);

            setTimeout(()=>{
                eventBus.$emit('scroll', payload.channel);
            },100);
        },
        setDirectChatSessions({commit}, sessions){
            commit(CHATSTORE_SET_DIRECT_CHAT_SESSIONS, sessions);
        },
        sendDirectTextMessage({state, rootState, dispatch}, session) {
            state.stompClient.send("/app/direct/message/" + session.directChatChannel, {},
                JSON.stringify({
                    'from': rootState.auth.name,
                    'text': session.directChatInputText,
                    'channel': {name: session.directChatChannel}
                }));
            dispatch('updateDirectChatSessionInputText',{session: session, text:""});
        },
        updateDirectChatSessionInputText({commit}, payload){
            commit(CHATSTORE_UPDATE_DIRECTCHATSESSION_TEXT, payload);
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

            //not pretty, but emitting a scroll event on an action invoked by a listener is not trivial.
            //this is the cleanest way I've found to notify all listener components
            setTimeout(()=>{
                eventBus.$emit('scroll', channel.name);
            },0);
        },
        closeDirectChatSession({commit}, session){
            commit(CHATSTORE_CLOSE_DIRECT_CHAT_SESSION, session);
        }
    }
};