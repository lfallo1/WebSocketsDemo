<template>
    <div>
        <li class="chat-list-item list-group-item" :class="'text-' + message.textClass"
            v-for="message in messages">
            <div class="message-label">
                <small :class="'text-' + message.textClass">
                    {{message.data.channel}}&nbsp;{{message.data.time}}
                </small>
                <div class="chat-list-item-message-text">{{message.data.value}}</div>
            </div>
        </li>
        <div ref="scrollTarget"></div>
    </div>
</template>

<script>

    import {mapState} from 'vuex';
    import {eventBus} from '../main.js';

    export default {
        created() {
            eventBus.$on('transcribeScroll', () => this.$scrollTo(this.$refs.scrollTarget, 200));
        },
        computed: {
            ...mapState({
                messages: state => state.chat.messages
            })
        }
    }

</script>

<style scoped>

    li {
        font-size: 18px;
    }

    #chat-window .message-label.pull-left span {
        font-size: 13px;
    }

    .chat-list-item {
        text-align: left;
    }

    .chat-list-item small {
        font-size: 12px;
        opacity: 0.65;
    }

    li.chat-list-item {
        padding: 2px 8px 2px 8px;
    }

    .chat-list-item-message-text {
        margin-top: -7px;
        font-weight: bold;
        white-space: pre-wrap;
    }


</style>