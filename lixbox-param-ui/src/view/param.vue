<template>
  <div>
    <confirm ref="confirm"></confirm>
    <v-row class="margin-10">
      <v-col md="3">
        <paramTree :items="params" @update:active="select" />
      </v-col>
      <v-col class="col-md-9 text-center">
        <v-scroll-y-transition mode="out-in">
          <div
            v-if="!selected"
            class="title grey--text text--lighten-1 font-weight-light"
            style="align-self: center;"
          >{{$t('param.service.select')}}
          </div>
          <v-card v-else class="pt-6 mx-auto" flat>
            <v-card-text>
              <h3 style="text-align:center">
                <v-text-field
                  :label="$t('param.service.entry.service')"
                  class="headline"
                  style="max-width:30%"
                  placeholder="lixbox-iam-core"
                  hide-details="auto"
                  v-model="selected.serviceId"
                ></v-text-field>
                <v-text-field
                  :label="$t('param.service.entry.code')"
                  class="headline mt-5"
                  style="max-width:30%"
                  placeholder="ICO_BATCH_ETAT"
                  hide-details="auto"
                  v-model="selected.code"
                ></v-text-field>
              </h3>
              <div class="blue--text mb-2"></div>
              <div class="blue--text subheading font-weight-bold"></div>
            </v-card-text>
            <v-divider></v-divider>
            <v-card-text>
              <v-row class="text-align:center" tag="v-card-text">
                <v-text-field
                  :label="$t('param.service.entry.libelle')"
                  placeholder="Etat du batch ICO"
                  hide-details="auto"
                  v-model="selected.libelle"
                ></v-text-field>
              </v-row>
              <v-row class="text-align:center" tag="v-card-text">
                <v-text-field
                  :label="$t('param.service.entry.type')"
                  placeholder="java.lang.Boolean"
                  hide-details="auto"
                  v-model="selected.valueClass"
                ></v-text-field>
              </v-row>
              <v-row class="text-align:center" tag="v-card-text">
                <v-textarea
                  v-model="selected.value"
                  :label="$t('param.service.entry.value')"
                  :auto-grow="true"
                  :clearable="true"
                  :filled="true"
                  :outlined="true"
                ></v-textarea>
              </v-row>
            </v-card-text>
            <v-card-actions>
              <v-btn
                color="primary"
                rounded
                outlined
                @click="save()"
              >{{$t("param.service.ui.button.entry.save")}}</v-btn>
              <v-btn
                color="error"
                rounded
                outlined
                @click="deleteParameter()"
              >{{$t("param.service.ui.button.entry.del")}}</v-btn>
            </v-card-actions>
          </v-card>
        </v-scroll-y-transition>
      </v-col>
    </v-row>
  </div>
</template>

<script>
/* eslint-disable */
import axios from "axios";
import confirm from "@/components/ui/confirmDialog.vue";
import paramTree from "@/components/ui/paramTree.vue";
import { ParametreService } from "@/api/ParametreService.js";
import { ENETRESET } from 'constants';

export default {
  components: {
    paramTree,
    confirm
  },
  name: "CacheView",
  data: () => ({
    parametreServiceUri: process.env.VUE_APP_PARAM_API_URI,
    params: [],
    selected: null,
    dialog: false,
    new: false,
    loading: true
  }),
  created() {
    this.initialize();
  },
  methods: {
    getParametreService() {
      return new ParametreService(this.parametreServiceUri);
    },
    async getConfiguration() {
      axios
        .get(process.env.VUE_APP_CONFIGURATION_URI)
        .then(response => {
          this.parametreServiceUri = response.data.paramApiUri;
          this.getParametres();
        })
        .catch(error =>{
          this.getParametres();
        });
    },
    initialize() {
      this.getConfiguration();
    },
    getParametres() {
      this.getParametreService()
        .getParametres()
        .then(data => {
          this.params = data;
        });
    },

    select(entry) {
      if (entry.length == 0) {
        this.selected = null;
      } else {
        if (entry[0].key == "new") {
          this.selected = { oid:"", code:"", value:"", valueClass:"", libelle:"", serviceId:""};
          this.new = true;
        } else {
          this.selected = this.getParametreService()
            .getParametreByCode(entry[0].serviceId, entry[0].code)
            .then(data => {
              this.selected = data;
            });
        }
      }
    },
    async updateEntry(index, oldValue, newValue) {
      this.selected.uris[index] = newValue;
    },
    async deleteEntry(index) {
      if (
        await this.$refs.confirm.open(
          this.$t("param.service.dialog.entry.uri.delete.title", [
            this.selected.name
          ]),
          this.$t("param.service.dialog.entry.uri.delete.text"),
          {
            color: this.$vuetify.theme.themes.light.error
          }
        )
      ) {
        this.selected.uris.splice(index, 1);
      }
    },
    save() {
      this.getParametreService()
        .synchronize(this.selected)
        .then(data => {
            this.params.push(this.selected);
        });
    },
    async deleteParameter() {
      if (
        await this.$refs.confirm.open(
          this.$t("param.service.dialog.entry.delete.title", [
            this.selected.code
          ]),
          this.$t("param.service.dialog.entry.delete.text"),
          {
            color: this.$vuetify.theme.themes.light.error
          }
        )
      ) {
        this.getParametreService()
          .remove(this.selected.oid)
          .then(data => {
            if (data) {
              this.cacheEntries.splice(
                this.cacheEntries.indexOf(this.selected),
                1
              );
            }
          });
      }
    }
  }
};
</script>

<style scoped>
.margin-5 {
  margin: 0.5rem 0.5rem 0 0.5rem;
}
.margin-10 {
  margin: 1rem 1rem 0 1rem;
}
</style>
