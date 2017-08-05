import Vue from "vue";
import Toast from 'vue-easy-toast'
import VueScrollTo from 'vue-scrollto';
import store from './store/store.js';

import TextMessages from "./components/TextMessages.vue";
import LoginForm from "./components/LoginForm.vue";
import Navbar from "./components/shared/Navbar.vue";
import Footer from "./components/shared/Footer.vue";
import Modal from 'modal-vue';
import config from './config.js';

Vue.use(VueScrollTo);
Vue.use(Toast);

//add components
Vue.component('app-textmessages', TextMessages);
Vue.component('app-navbar', Navbar);
Vue.component('app-footer', Footer);
Vue.component('app-loginform', LoginForm);
Vue.component('modal', Modal);

//used exclusively for emitting scroll events triggered after websocket message handler events
//i.e., scroll to direct chat message textbox when another users starts a chat with you (the client)
export const eventBus = new Vue();

//set csrf header
config.setCsrfHeader();

new Vue({
    el: '#app',
    store
});
