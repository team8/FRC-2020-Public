import React from 'react';
import Link from 'next/link'
import NavBar from "../components/navbar"
import fetchWithTimeout from "../components/fetch"

class OutputBox extends React.Component {
    async getFromApi(){
        try { 
            let result = await fetchWithTimeout('http://10.0.8.2:4000');
            result = await result.json();
            result = result.config;
            let newConfig = result;
            return newConfig; 
        } catch(error) {
            this.setState({timedOut: true})
            return false;
        }

    }

    getData = () => {
        this.getFromApi().then((result) => {
            if(!result){
                return;
            }
            let newConfig = result;
            if (!newConfig){
                return
            }
            for(const item of Object.keys(newConfig)){
                for(const subitem of Object.keys(newConfig[item])){
                    if(newConfig[item][subitem] == null){
                        newConfig[item][subitem] = "null";
                    } else if(typeof newConfig[item][subitem] === "object"){
                        for(const subsubitem of Object.keys(newConfig[item][subitem])){
                            if(newConfig[item][subitem][subsubitem] == null){
                                newConfig[item][subitem][subsubitem] = "null";
                            }
                        }
                    }
                }
            }
            this.setState({
                newInfoLoaded : true,
                config: JSON.parse(JSON.stringify(newConfig)),
                cachedConfig: JSON.parse(JSON.stringify(newConfig))
            });
        });

    }

    constructor(props) {
        super(props);
        this.intervalID = null;
        this.state = {
            text : null,
            newInfoLoaded : false,
            config: {},
            timedOut: false,
            tab: null,
            subtab: null
        };
        this.save = this.save.bind(this);
        this.refresh = this.refresh.bind(this);
    }


    componentDidMount() {
        this.getData();
    }

    componentWillUnmount() {
    }

    refresh(e) { 
        this.getData();
        this.forceUpdate();
        //window.location.reload()
    }

    save(e) {
        let config = this.state.config;
        let newConfig = {};
        for(const item of Object.keys(this.state.config)){
            for(const subitem of Object.keys(this.state.config[item])){
                if(subitem == "checkFaults"){
                }
                if(this.state.config[item][subitem] !== null && typeof this.state.config[item][subitem] == "object"){
                    for(const subsubitem of Object.keys(this.state.config[item][subitem])){
                        if(config[item][subitem][subsubitem] != this.state.cachedConfig[item][subitem][subsubitem]){
                            if(!newConfig[item].hasOwnProperty(subitem)){
                                newconfig[item][subitem] = {}
                            } 
                            newConfig[item][subitem][subsubitem] = config[item][subitem][subsubitem];
                        }
                    }
                } else if(config[item][subitem] != this.state.cachedConfig[item][subitem]){
                    if(!newConfig.hasOwnProperty(item)){
                        newConfig[item] = {}
                    }
                    newConfig[item][subitem] = config[item][subitem]
                }
            }
        }
        if(Object.keys(newConfig).length === 0){
            alert("Nothing to save!");
            return 0;
        }
        this.setState({
            cachedConfig: JSON.parse(JSON.stringify(this.state.config))
        });
        const requestOptions = {
            method: 'POST',
            headers: {'Content-Type': 'text/plain'},
            body: JSON.stringify(newConfig)
        }
        fetch("http://10.0.8.2:4000", requestOptions)
            .then(response => response.text)
            .then((result) => {
                console.log("worked");
            });
    }

    render() {
        if (this.state.error) {
            return <div>Error: {this.state.error.message}</div>;
        } else if(this.state.timedOut){
            return <div class="text-4xl font-bold text-red-600 text-center">Request Timed Out! Make sure you're connected to the robot wifi</div>
        } else if (this.state.newInfoLoaded === false) {
            return <div>Loading...</div>;
        } else {
            let objs = []
            for(const item of Object.keys(this.state.config)){
                let inputs = []
                for(const subitem of Object.keys(this.state.config[item])){
                    let inter;
                    const update = (e, i=item, s=subitem) => {
                        this.getFromApi().then((newConfig) => {
                            let config = this.state.config;
                            config[i][s] = newConfig[i][s];
                            this.setState({
                                config: JSON.parse(JSON.stringify(config)),
                            });
                        });
                    }
                    if(this.state.config[item][subitem] !== null && typeof this.state.config[item][subitem] == "object"){
                        inter = []

                        for(const subsubitem of Object.keys(this.state.config[item][subitem])){
                            const update = (e, i=item, s=subitem, ss=subsubitem) => {
                                this.getFromApi().then((newConfig) => {
                                    let config = this.state.config;
                                    config[i][s][ss] = newConfig[i][s][ss];
                                    this.setState({
                                        config: JSON.parse(JSON.stringify(config))
                                    })
                                })
                            }
                            let bool = false;
                            let type = "text";
                            if(typeof(this.state.config[item][subitem][subsubitem]) === "boolean"){
                                bool = true;
                                type = "checkbox"
                            }    
                            const onChange = (e, i=item, s=subitem, ss=subsubitem) => {
                                let config = this.state.config;
                                let value;
                                if(bool){
                                    value = e.target.checked;
                                } else {
                                    value = e.target.value;
                                }
                                if(e.target.value && !isNaN(e.target.value)){
                                    value = parseFloat(value)
                                }
                                config[i][s][ss] = value;
                                this.setState({config:config})
                            }
                            let inp;
                            if(bool && this.state.config[item][subitem][subsubitem]) {

                                inp = <input class="text-white bg-gray-800" type={type} value={this.state.config[item][subitem][subsubitem]} onChange={onChange} checked></input>
                            } else {
                                inp = <input class="text-white bg-gray-800" type={type} value={this.state.config[item][subitem][subsubitem]} onChange={onChange}></input>
                            }
                            inter.push(

                                <div class="text-center">
                                  {subsubitem}<br/>
                                  <button class="bg-gray-500 hover:bg-gray-700 text-white font-bold rounded text-center text-sm" onClick={update}>Refresh</button>
                                  <br/>
                                  {inp}
                                </div>
                            )
                        }
                    } else {

                        let bool = false;
                        let type = "text";
                        if(typeof(this.state.config[item][subitem]) === "boolean"){
                            bool = true;
                            type = "checkbox"
                        }

                        const onChange = (e, i=item, s=subitem) => {
                            let config = this.state.config;
                            let value;
                            if(bool){
                                value = e.target.checked;
                            } else {
                                value = e.target.value;
                            }
                            if(e.target.value && !isNaN(e.target.value)){
                                if(e.target.value.charAt(e.target.value.length -1) !== "."){
                                    value = parseFloat(value)
                                }
                            }
                            config[i][s] = value;
                            this.setState({config:config})
                        }
                        if(bool && this.state.config[item][subitem]){
                            inter = <input class="text-white bg-gray-800" type={type} value={this.state.config[item][subitem]} onChange={onChange} checked></input>
                        } else {
                            inter = <input class="text-white bg-gray-800" type={type} value={this.state.config[item][subitem]} onChange={onChange}></input>
                        }
                    }

                    let hidden = "text-center"
                    if(this.state.subtab !== subitem){
                        hidden = hidden.concat(" ", "hidden")
                    }

                    inputs.push(
                      <div class={hidden}>
                        {subitem}<br/>
                        <button class="bg-gray-500 hover:bg-gray-700 text-white font-bold rounded text-center text-sm" onClick={update}>Refresh</button>
                        <br/>
                        {inter}
                      </div>
                    )
                }
                const update = (e, i=item) => {
                    this.getFromApi().then((newConfig) =>{
                        let config = this.state.config;
                        config[i] = newConfig[i];
                        this.setState({
                            config: JSON.parse(JSON.stringify(config)),
                        });
                    })
                }
                let hidden = "text-center bg-gray-700";
                if(this.state.tab !== item){
                    hidden = hidden.concat(" ", "hidden");
                }
                let tabs = [];
                for(const subtab of Object.keys(this.state.config[item])){
                    const switchTab = (e, items = subtab) => {
                        this.setState({subtab:items});
                    }
                    tabs.push(
                        <button class =  "bg-gray-500 hover:bg-gray-700 text-white font-bold py-1 px-3 rounded text-center text-sm" onClick={switchTab}>{subtab}</button>
                    )
                }
                objs.push(
                  <div class={hidden}>
                    <span class="text-4xl">{item}</span>
                    <br/>
                    <button class="bg-gray-500 hover:bg-gray-700 text-white font-bold rounded text-center text-base" onClick={update}>Refresh Group</button> 
                    <br/>
                    <div class="text-center">
                        {tabs}
                    </div>
                    {inputs}
                  </div>
                )
            }  

            let tabs = [];
            for(const item of Object.keys(this.state.config)){
                const switchTab = (e, items = item) => {
                    this.setState({tab:items});
                }
                tabs.push(
                    <button class =  "bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded text-center" onClick={switchTab}>{item}</button>
                )
            }

            return (
                <>
                    <div class = "bg-gray-900 w-screen h-screen font-mono text-white">
                        <NavBar></NavBar>
                        <div class="text-center bg-gray-600">
                            <button class="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded text-center" value="save" onClick={this.save}>save</button>
                            <button class="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded text-center" value="refresh" onClick={this.refresh}>refresh all</button>
                        </div>
                        <div class="text-center">
                            {tabs}
                        </div>
                        {objs}
                    </div>
                </>
            )
        }
    }

}
export default function Output(){
  return <OutputBox/>
}
