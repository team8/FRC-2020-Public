import React from 'react';
import Link from 'next/link'
import NavBar from "../components/navbar"

class OutputBox extends React.Component {
    getData = () => {
        fetch('http://10.0.8.2:4000')
            .then(res => res.json())
            .then((result) => {
                //console.log(result.testObj);
                //apiResult = result.testObj;
                //let newConfig = result.config;
                this.intervalID = setTimeout(this.getData.bind(this), 1);
                this.setState({
                    newInfoLoaded : true,
                    data: result.telemetry,
                });
            },
            (error) => {
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
            data: {}
        };
    }


    componentDidMount() {
        this.getData()
    }

    componentWillUnmount() {
        clearTimeout(this.intervalID);

    }


    render() {
        if (this.state.error) {
            return <div>Error: {this.state.error.message}</div>;
        } else if (this.state.newInfoLoaded === false) {
            return <div>Loading...</div>;
        } else {
            let entries = [];
            for (const key of Object.keys(this.state.data)){
                entries.push(
                    <>
                        <div>
                            <span class="text-white text-3xl"> {key}: </span> 
                            <span class="text-white"> {this.state.data[key]} </span> 
                            <br/>
                        </div>
                    </>
                )
            }
            return (
                <>
                    <div class = "bg-gray-900 w-screen h-screen font-mono text-white">
                        <NavBar></NavBar>
                        <div class="flex flex-col justify-center items-center">
                            {entries}
                        </div>
                    </div>
                </>
            )
        }
    }

}
export default function Output(){
  return <OutputBox/>
}
