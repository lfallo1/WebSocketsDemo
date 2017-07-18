import Vue from "vue";
import TextMessages from "./TextMessages.vue";
import Navbar from "./Navbar.vue";
import config from './config.js';

//add components
Vue.component('app-textmessages', TextMessages);
Vue.component('app-navbar', Navbar);

export const eventBus = new Vue();

//set csrf header
config.setCsrfHeader();

new Vue({
    el: '#app'
});
