<template>
    <div class="container">
        <div class="navbar navbar-default navbar-fixed-bottom text-center">
            <div class="container">
                <p class="navbar-text">2017 - Lance Fallon</p>
            </div>
        </div>
    </div>
</template>

<script>
    import axios from 'axios';

    export default {
        data() {
            return {
                gitHash: ""
            }
        },
        created() {
//            axios.get('/api/config/gitinfo')
//                .then(res => {
//                    this.gitHash = res.data.commitIdAbbrev
//                })
//                .catch(err => console.log(err));
        }
    }
</script>

<style scoped>

</style>