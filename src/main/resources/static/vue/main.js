import Vue from "vue";
import VueScrollTo from 'vue-scrollto';
import TextMessages from "./TextMessages.vue";
import Navbar from "./Navbar.vue";
import Footer from "./Footer.vue";
import LoginForm from "./LoginForm.vue";
import config from './config.js';

Vue.use(VueScrollTo);

//add components
Vue.component('app-textmessages', TextMessages);
Vue.component('app-navbar', Navbar);
Vue.component('app-footer', Footer);
Vue.component('app-loginform', LoginForm);

export const eventBus = new Vue();

//set csrf header
config.setCsrfHeader();

new Vue({
    el: '#app'
});
