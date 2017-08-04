<template>
    <div>
        <div id="login-form-wrapper" class="col-md-4 col-md-offset-4 col-sm-6 col-sm-offset-3">
            <h2>Login</h2>
            <div>
                <form id="login-form" class="form-horizontal" @submit.prevent.stop="submit">

                    <div class="form-group">
                        <div class="col-sm-12">
                            <input v-model="form.username" type="text" class="form-control" name="username"
                                   id="username" placeholder="Username"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-sm-12">
                            <input v-model="form.password" type="password" class="form-control" name="password"
                                   id="password" placeholder="Password"/>
                        </div>
                    </div>

                    <button id="login-button" type="submit" class="btn btn-primary">Submit</button>

                    <div id="login-error" v-if="error" class="alert alert-danger text-center">
                        <span class="glyphicon glyphicon-remove-circle"></span>&nbsp;<span>Invalid username / password</span>
                    </div>
                </form>
            </div>
        </div>
    </div>
</template>

<script>

    import config from '../config.js';
    import axios from 'axios';

    export default {
        data() {
            return {
                form: {"username": "", "password": ""},
                csrf: "",
                error: false
            }
        },
        methods: {
            submit() {
                this.error = false;
                const data = 'username=' + this.form.username + '&password=' + this.form.password + "&token=" + this.csrf;
                axios({
                    url: '/login',
                    method: 'POST',
                    data: data
                }).then(res => {
                    window.location.href = "/";
                }).catch(err => {
                    this.error = true;
                    console.log(err);
                })
            }
        },
        created() {
            this.csrf = config.getCsrfHeader();
        }
    }
</script>

<style scoped>
    #login-form-wrapper {
        margin-bottom: 100px;
    }

    #login-form-wrapper #login-button {
        width: 100%;
        margin-bottom: 15px;
        font-size: 20px;
    }

    #login-form-wrapper #login-error {
        padding: 8px;
    }

    #login-error span{
        font-size: 14px;
    }

    #login-form-wrapper h2 {
        color: rgba(51, 122, 183, 0.8);
        text-align: center;
    }
</style>