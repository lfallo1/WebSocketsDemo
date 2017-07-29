import Vue from "vue";
import VueScrollTo from 'vue-scrollto';
import TextMessages from "./components/TextMessages.vue";
import LoginForm from "./components/LoginForm.vue";
import Navbar from "./components/shared/Navbar.vue";
import Footer from "./components/shared/Footer.vue";
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
