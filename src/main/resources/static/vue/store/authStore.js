import axios from 'axios';

export default {
    state: {
        auth: {}
    },
    mutations: {
        setAuth(state, auth) {
            state.auth = auth;
        }
    },
    actions: {
        AUTHSTORE_FETCH_USER({commit}){
            axios.get('api/user')
                .then(res => {
                    commit('setAuth', res.data)
                })
                .catch(err => commit('setAuth', {}));
        }
    }
};