import Vue from "vue";
import TextInput from "./TextInput.vue";
import config from "./config.js";

//add components
Vue.component('app-textinput', TextInput);

//set csrf header
config.setCsrfHeader();

new Vue({
    el: '#app'
});
