<template>
    <div class="footer">
        <div class="navbar navbar-default navbar-fixed-bottom text-center">
            <div class="container">
                <p class="navbar-text">2017 - Jim Cosby & Lance Fallon (version: {{gitInformation.commitIdAbbrev}})</p>
            </div>
        </div>
    </div>
</template>

<script>
    import axios from 'axios';
    import {eventBus} from '../../main.js';

    export default {
        data() {
            return {
                gitInformation: {}
            }
        },
        created() {
            eventBus.$on('gitinfo', (data) => this.gitInformation = data.value);

            axios.get('/api/config/gitinfo')
                .then(res => eventBus.$emit('gitinfo', {value: res.data}))
                .catch(err => console.log(err));
        }
    }
</script>

<style scoped>

</style>