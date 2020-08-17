import axios from "axios";

export class ParametreService {
  API_URL = "";
  DEFAULT_REQUEST_CONF = {
    headers: {
      "Content-Type": "application/json",
    },
  };

  constructor(url) {
    this.API_URL = url;
  }
  getParametres(){
    const url = `${this.API_URL}/params/`;
    return axios.get(url).then((response) => response.data);
  }

  getParametreByCode(serviceId, code){
    const url = `${this.API_URL}/${serviceId}/param/${code}`;
    return axios.get(url).then((response) => response.data);

  }

  synchronize(param){
    const url = this.API_URL + "/param/sync";
    return axios.post(url, param).then((response) => response.data);
  }
  
  remove(key) {
    const url = this.API_URL + "/keys/" + key;
    return axios.delete(url).then((response) => response.data);
  }
}
