import Vue from 'vue';
import Vuex from 'vuex';
import axios from 'axios';
import config from '../config.js';
import ChatStore from './chat/chatStore.js';
import {ROOTSTORE_SET_AUTH, ROOTSTORE_SET_CSRF} from './mutation-types.js';

Vue.use(Vuex);
export default new Vuex.Store({
    modules: {
        chat: ChatStore
    },
    state: {
        auth: {},
        csrf: ""
    },
    mutations: {
        [ROOTSTORE_SET_AUTH](state, auth) {
            state.auth = auth;
        },
        [ROOTSTORE_SET_CSRF](state, csrf){
            state.csrf = csrf;
        }
    },
    actions: {
        fetchUser({commit}) {
            axios.get('api/user')
                .then(res => {
                    commit(ROOTSTORE_SET_AUTH, res.data)
                })
                .catch(err => commit(ROOTSTORE_SET_AUTH, {}));
        },
        setCsrf({commit}){
            const csrf = config.getCsrfHeader();
            commit(ROOTSTORE_SET_CSRF, csrf);
        },
        keepAlive({state}){
            if(state.auth.name){
                axios.get('api/user/keepalive')
                    .then(()=>console.log('keep session alive'))
                    .catch((err)=> console.log('##ERROR keeping session alive## -> ', err));
            }
        }
    }
});