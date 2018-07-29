<template>
    <div class="faq-container">

        <h3>{{ title }}</h3>

    </div>
</template>

<script>

    import {mapActions} from 'vuex';

    export default {
        data() {
            return {
                title: 'Under Construction'
            }
        },

        methods: {
            ...mapActions({
                setHideConnect: 'chat/setHideConnect',
                fetchUser: 'fetchUser',
            })
        },
        created() {
            this.fetchUser();
            this.setHideConnect(true);
        }
    }
</script>