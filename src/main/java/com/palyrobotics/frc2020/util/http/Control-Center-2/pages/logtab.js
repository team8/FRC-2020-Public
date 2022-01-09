import React from 'react';
import Link from 'next/link'
import NavBar from "../components/navbar"
import fetchWithTimeout from "../components/fetch"

class OutputBox extends React.Component {
    getData = () => {
        fetch('http://10.0.8.2:4000')
        //fetch('http://localhost:4000')
            .then(res => res.json())
            .then((result) => {
                //console.log(result.testObj);
                //apiResult = result.testObj;
                console.log(result.logs)
                this.intervalID = setTimeout(this.getData.bind(this), 1);
                this.setState({
                    logs : result.logs.flat(),
                    newInfoLoaded : true
                });
            },
            (error) => {
                if(error.name === 'AbortError'){
                    this.setState({timedOut:true})
                }
                this.intervalID = setTimeout(this.getData.bind(this), 1);
            }
        )
    }

    constructor(props) {
        super(props);
        this.intervalID = null;
        this.state = {
            text : null,
            newInfoLoaded : false,
            logs: [],
            timedOut: false
        };
    }

    componentDidMount() {
        this.getData();
    }

    componentWillUnmount() {
        clearTimeout(this.intervalID);
    }

    render() {
        const { error, newInfoLoaded, text, logs, timedOut} = this.state;
        if (error) {
            return <div>Error: {error.message}</div>;
        }else if(timedOut){
            return <div class="text-4xl font-bold text-red-600 text-center">Request Timed Out! Make sure you're connected to the robot wifi</div>
        }else if (newInfoLoaded === false) {
            return <div>Loading...</div>;
        } else {
            let logss = []
            for(const log of this.state.logs){
                logss.push(<React.Fragment>{log}<br /></React.Fragment>)
            }
            //return <div style={{width:"100%",height:500,overflow:"scroll",display: "flex", "flex-direction": "column-reverse", "background-color":"#333333", color:"white"}}>{logss}</div>;
            return (
                <div class = "bg-gray-900 w-screen h-screen font-mono">
                    <NavBar></NavBar>
                    <div style = {{width:"80%", height:"80%"}} class="m-2 bg-gray-600 text-white flex overflow-scroll flex-col-reverse">{logss}</div>
                    {/*<div style={{width:"100%",height:500,overflow:"scroll",display: "flex", "flex-direction": "column-reverse", "background-color":"#333333", color:"white"}}>{logss}</div>*/}
                </div>
            )
        }
    }

}
export default function Output(){
  return <OutputBox/>
}
