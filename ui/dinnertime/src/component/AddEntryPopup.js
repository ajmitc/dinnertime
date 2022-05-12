import React, { Component } from "react";

export default class AddEntryPopup extends Component {
    constructor(props){
        super(props);
        this.state = {
            toggle: props.toggle,
            textvalue: ''
        };
        this.handleTextChange = this.handleTextChange.bind(this);
    }

    handleTextChange(e){
        if (e.keyCode === 13){
            console.log("Search for " + e.target.value);
            e.preventDefault();
        }
        else {
            this.setState({textvalue: e.target.value});
        }
    }

    handleClick = () => {
        this.props.toggle();
    }

    render(){
        return (
            <div className="modal">
                <div className="modal-content">
                    <span className="close-button" onClick={this.handleClick}>&times;</span>
                    <div className="add-entry-input-container"><input type='text' onKeyDown={this.handleTextChange}/></div>
                    <div className="add-entry-search-results"></div>
                    <div className="add-entry-favorites"></div>
                </div>
            </div>
        );
    }
}
