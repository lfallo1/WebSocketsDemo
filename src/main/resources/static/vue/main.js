import Vue from "vue";
import TextMessages from "./TextMessages.vue";
import Navbar from "./Navbar.vue";
import Footer from "./Footer.vue";
import config from './config.js';

//add components
Vue.component('app-textmessages', TextMessages);
Vue.component('app-navbar', Navbar);
Vue.component('app-footer', Footer);

export const eventBus = new Vue();

//set csrf header
config.setCsrfHeader();

new Vue({
    el: '#app'
});
