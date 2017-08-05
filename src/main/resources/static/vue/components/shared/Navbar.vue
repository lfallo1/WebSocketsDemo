<template>
    <div id="nav-wrapper">
        <nav class="navbar navbar-default">
            <div class="container-fluid">
                <!-- Brand and toggle get grouped for better mobile display -->
                <div class="navbar-header">
                    <button id="toggle-nav" class="navbar-toggle" :class="{'collapsed' : !ariaExpanded}"
                            data-toggle="collapse"
                            data-target="#bs-example-navbar-collapse-1" :aria-expanded="ariaExpanded"
                            @click="ariaExpanded=!ariaExpanded">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand" href="">Websocket Demo</a>
                    <ul class="nav navbar-nav">
                        <li class="connected-navbar-message"
                            :class="{'connected':connected, 'disconnected':!connected}">
                            <a @click.prevent.stop href="">
                                {{connected ? 'CONNECTED (' + usersConnected.length + ' users logged in)' : 'DISCONNECTED'}}
                                <br>
                                <span class="text-primary">{{subscribedText}}</span>
                            </a>
                            <button class="btn btn-danger" v-if="connected" @click="disconnect"><span
                                    class="glyphicon glyphicon-remove"></span>Disconnect
                            </button>
                            <button v-else class="btn btn-success" @click="connect"><span
                                    class="glyphicon glyphicon-signal"></span>&nbsp;Connect to server
                            </button>
                        </li>
                    </ul>
                </div>

                <!-- Collect the nav links, forms, and other content for toggling -->
                <div :class="{'collapse' : !ariaExpanded}" class="navbar-collapse" id="bs-example-navbar-collapse-1" @click="ariaExpanded=!ariaExpanded">
                    <ul class="nav navbar-nav navbar-right">
                        <li><a href="">{{auth.name ? 'Signed in as ' + auth.name : 'Not signed in'}}</a></li>
                        <li class="dropdown" :class="{'open' : navbarDropdownIsOpen}">
                            <a @click.prevent.stop="navbarDropdownIsOpen = !navbarDropdownIsOpen" href=""
                               class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true"
                               :aria-expanded="ariaExpanded">Account <span class="caret"></span></a>
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

    import config from '../../config.js';
    import {mapState, mapGetters, mapActions} from 'vuex';

    export default {
        data() {
            return {
                navbarDropdownIsOpen: false,
                ariaExpanded: false
            }
        },
        computed: {
            ...mapGetters({
                subscribedText: 'chat/subscribedText'
            }),
            ...mapState({
                auth: state => state.auth,
                channelSubscriptions: state => state.chat.channelSubscriptions,
                channelParticipants: state => state.chat.channelParticipants,
                connected: state => state.chat.connected,
                usersConnected: state => state.chat.usersConnected
            })
        },
        methods: {
            ...mapActions({
                connect: 'chat/connect',
                disconnect: 'chat/disconnect',
                setConnected: 'chat/setConnected'
            }),
            toggleNavCollapsed() {
                this.navCollapsed = !this.navCollapsed
            }
        }
    }

</script>

<style scoped>

    #toggle-nav {
        z-index: 1000;
    }

    li.disconnected a, li.connected a {
        font-weight: bold;
    }

    li.disconnected a {
        color: #d9534f !important;
    }

    li.connected a {
        color: #449d44 !important;
    }

    li.connected-navbar-message a {
        display: inline-block;
    }

</style>