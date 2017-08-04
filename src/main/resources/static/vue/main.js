import Vue from "vue";
import Vuex from 'vuex';
import VueScrollTo from 'vue-scrollto';
import {ROOTSTORE_SET_AUTH} from './store/mutation-types.js';
import ChatStore from './store/chatStore.js';
import axios from 'axios';

import TextMessages from "./components/TextMessages.vue";
import LoginForm from "./components/LoginForm.vue";
import Navbar from "./components/shared/Navbar.vue";
import Footer from "./components/shared/Footer.vue";
import Modal from 'modal-vue';
import config from './config.js';

Vue.use(Vuex);
Vue.use(VueScrollTo);

//add components
Vue.component('app-textmessages', TextMessages);
Vue.component('app-navbar', Navbar);
Vue.component('app-footer', Footer);
Vue.component('app-loginform', LoginForm);
Vue.component('modal', Modal);

export const eventBus = new Vue();

//set csrf header
config.setCsrfHeader();

const store = new Vuex.Store({
    modules: {
        chat: ChatStore
    },
    state: {
        auth: {}
    },
    mutations: {
        [ROOTSTORE_SET_AUTH](state, auth) {
            state.auth = auth;
        }
    },
    actions: {
        fetchUser({commit}){
            axios.get('api/user')
                .then(res => {
                    commit(ROOTSTORE_SET_AUTH, res.data)
                })
                .catch(err => commit(ROOTSTORE_SET_AUTH, {}));
        }
    }
});

new Vue({
    el: '#app',
    store
});
