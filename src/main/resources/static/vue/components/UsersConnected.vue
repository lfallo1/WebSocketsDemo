<template>
    <div id="connected-users-container">
        <div class="connected-users-label">
            <i class="material-icons with-text md-18">recent_actors</i>&nbsp;Connected users
        </div>
        <div v-if="connected">
            <span class="connected-user" v-for="(username, idx) in usersConnected">
                <span :class="{'clickable text-primary' : auth.name && auth.name != username}" @click="directChatRequest(username)">{{username}}{{idx < usersConnected.length-1 ? ', ' : ''}}</span>
            </span>
            <span class="no-connected-users-text" v-if="usersConnected.length == 0">There are currently no connected users</span>
        </div>
        <div v-else>
            <span class="no-connected-users-text">Must be connected to server to view connected users</span>
        </div>
    </div>
</template>

<script>

    import {mapState, mapActions} from 'vuex';

    export default{
        methods: {
            ...mapActions({
                directChatRequest: 'chat/directChatRequest'
            })
        },
        computed:{
            ...mapState({
                auth: state => state.auth,
                connected: state => state.chat.connected,
                usersConnected: state => state.chat.usersConnected
            })
        }
    }
</script>

<style scoped>

    #connected-users-container{
        background: #333;
        width: 100%;
        margin-top: -20px;
        margin-bottom: 25px;
        padding: 4px;
        text-align: left;
        border: 1px solid black;
        min-height: 30px;
    }

    #connected-users-container .connected-user{
        color: white;
        font-weight: bold;
    }

    .connected-users-label{
        float: left;
        color: #ccc;
        margin-left: 10px;
        margin-right: 12px;
        font-size: 10px;
    }

    .no-connected-users-text{
        color: #d9534f;
    }
</style>
