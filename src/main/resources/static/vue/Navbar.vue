<template>
    <div id="nav-wrapper">
        <nav class="navbar navbar-default">
            <div class="container-fluid">
                <!-- Brand and toggle get grouped for better mobile display -->
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand" href="#">Websocket Demo</a>
                    <ul class="nav navbar-nav">
                        <li class="connected-navbar-message" :class="{'connected':connected, 'disconnected':!connected}">
                            <a href="">{{connected ? 'CONNECTED' : 'DISCONNECTED'}}</a>
                        </li>
                    </ul>
                </div>

                <!-- Collect the nav links, forms, and other content for toggling -->
                <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                    <ul class="nav navbar-nav navbar-right">
                        <li><a href="">{{auth.name ? 'Signed in as ' + auth.name : 'Not signed in'}}</a></li>
                        <li class="dropdown" :class="{'open' : navbarDropdownIsOpen}">
                            <a @click.prevent.stop="navbarDropdownIsOpen = !navbarDropdownIsOpen" href="" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Account <span class="caret"></span></a>
                            <ul class="dropdown-menu" v-if="!auth.name">
                                <li><a href="/login">Signin</a></li>
                            </ul>
                            <ul class="dropdown-menu" v-else>
                                <li><a href="/logout">Logout</a></li>
                            </ul>
                        </li>
                    </ul>
                </div><!-- /.navbar-collapse -->
            </div><!-- /.container-fluid -->
        </nav>
    </div>
</template>

<script>

    import { eventBus } from './main.js';

    export default {
        data(){
            return {
                auth: {},
                subscribed: false,
                connected: false,
                navbarDropdownIsOpen: false
            }
        },
        created(){
            eventBus.$on('connected', (data)=> this.connected = data.value);
            eventBus.$on('subscribed', (data)=> this.subscribed = data.value);
            eventBus.$on('auth', (data)=> this.auth = data.value);
        }
    }

</script>

<style scoped>
    li.disconnected a, li.connected a{
        font-weight: bold;
    }

    li.disconnected a{
        color: #d9534f !important;
    }

    li.connected a{
        color: #449d44 !important;
    }
</style>