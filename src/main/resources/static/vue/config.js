import axios from 'axios';

export default{

    setCsrfHeader : function(){
      if($('meta[name="csrf-token"]')){
        console.log('setting csrf header: ' + $('meta[name="csrf-token"]').attr('content'));
        axios.defaults.headers.common['X-CSRF-Token'] = $('meta[name="csrf-token"]').attr('content');
      } else{
        console.log('header not found');
      }
    },
    getCsrfHeader : function(){
        if($('meta[name="csrf-token"]')) {
            return $('meta[name="csrf-token"]').attr('content')
        }
        return null;
    },
    unique: function(arr) {
        var u = {}, a = [];
        for(var i = 0, l = arr.length; i < l; ++i){
            if(!u.hasOwnProperty(arr[i])) {
                a.push(arr[i]);
                u[arr[i]] = 1;
            }
        }
        return a;
    }

}