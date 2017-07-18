import Vue from "vue";
import TextMessages from "./TextMessages.vue";
// import UserInput from "./UserInput.vue";
import config from './config.js';

//add components
Vue.component('app-textmessages', TextMessages);
// Vue.component('app-userinput', UserInput);

//set csrf header
config.setCsrfHeader();

new Vue({
    el: '#app'
});
