import Vue from "vue";
import Router from "vue-router";
import Param from "@/view/param.vue";

Vue.use(Router);

export default new Router({
  mode: 'history',
  routes: [
    {
      path: "/",
      redirect: { name: "param" },
    },
    {
      path: "/param",
      name: "param",
      component: Param,
    },
  ],
});
