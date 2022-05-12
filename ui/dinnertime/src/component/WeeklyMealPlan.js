import React from 'react';
import AddEntryPopup from "./AddEntryPopup"

class DailyMealPlanEntry extends React.Component {
  render() {
    {/*
            id: "1234567890",
            mealPlanId: "1",
            type: "text",
            content: "Leftovers",
            notes: "",
            category: "",
            date: "2022-05-09"
    */}
    let content;
    if (this.props.entry.type == "text"){
        content = this.props.entry.content;
    }
    else if (this.props.entry.type == "url"){
        content = <a href={this.props.entry.content}>{this.props.entry.content}</a>;
    }
    else if (this.props.entry.type == "drive_id"){
        content = "Drive: " + this.props.entry.content;
    }
    return (
        <div className="mealplan-entry">
            {content}
        </div>
    );
  }
}

class DailyMealPlanEntries extends React.Component {
    constructor(props){
        super(props);
        this.state = {
            category: props.category,
            entrylist: props.entrylist,
            seen: false
        };
        this.onButtonClick = this.onButtonClick.bind(this);
    }

    onButtonClick(e){
        // Open popup dialog with search box, recommended recipes, favorite recipes
        this.setState({seen: !this.state.seen});
    }

    render() {
        let categoryHeader = <span className="mealplan-category-label">{this.props.category}</span>;
        if (this.props.category == "default"){
            categoryHeader = "";
        }

        return (
            <div className="mealplan-entries">
                {categoryHeader}
                {this.props.entrylist.map((entry) => <DailyMealPlanEntry key={entry.id} entry={entry} />)}
                <button className="mealplan-add-entry-button" onClick={this.onButtonClick}>+</button>
                {this.state.seen ? <AddEntryPopup toggle={this.onButtonClick}/> : null}
            </div>
        );
    }
}

class DailyMealPlanCategories extends React.Component {
  render() {
    return (
      <div className="mealplan-categories">
          {Object.entries(this.props.entries).map(([category, entrylist]) => <DailyMealPlanEntries key={category} category={category} entrylist={entrylist} />)}
      </div>
    );
  }
}

class DailyMealPlan extends React.Component {
  render() {
    return (
      <div className="daily-mealplan-container">
        <div className="daily-mealplan-header">
            {this.props.dayname}
        </div>
        <div className="daily-mealplan-body">
            <DailyMealPlanCategories entries={this.props.entries} />
        </div>
      </div>
    );
  }
}

class WeeklyMealPlan extends React.Component {
    constructor(props){
        super(props);

        const entries = {
            days: [
                {
                    date: "2022-05-09",
                    dayOfWeek: "Monday",
                    entries: {
                        "default": [
                            {
                                id: "1234567890",
                                mealPlanId: "1",
                                type: "text",
                                content: "Leftovers",
                                notes: "This is a note",
                                category: "",
                                date: "2022-05-09"
                            },
                            {
                                id: "2345678901",
                                mealPlanId: "1",
                                type: "url",
                                content: "http://recipes.com/foobar",
                                notes: "This is a note",
                                category: "",
                                date: "2022-05-09"
                            },
                            {
                                id: "3456789012",
                                mealPlanId: "1",
                                type: "drive_id",
                                content: "my-drive-id",
                                notes: "This is a note",
                                category: "",
                                date: "2022-05-09"
                            },
                        ]
                    }
                },
                {
                    date: "2022-05-10",
                    dayOfWeek: "Tuesday",
                    entries: {
                        "Breakfast": [
                            {
                                id: "1234567890",
                                mealPlanId: "1",
                                type: "text",
                                content: "Leftovers",
                                notes: "This is a note",
                                category: "",
                                date: "2022-05-10"
                            }
                        ]
                    }
                },
                {
                    date: "2022-05-11",
                    dayOfWeek: "Wednesday",
                    entries: {
                        "default": [
                            {
                                id: "1234567890",
                                mealPlanId: "1",
                                type: "text",
                                content: "Leftovers",
                                notes: "This is a note",
                                category: "",
                                date: "2022-05-11"
                            }
                        ]
                    }
                },
                {
                    date: "2022-05-12",
                    dayOfWeek: "Thursday",
                    entries: {
                        "default": [
                            {
                                id: "1234567890",
                                mealPlanId: "1",
                                type: "text",
                                content: "Leftovers",
                                notes: "This is a note",
                                category: "",
                                date: "2022-05-12"
                            }
                        ]
                    }
                },
                {
                    date: "2022-05-13",
                    dayOfWeek: "Friday",
                    entries: {
                        "default": [
                            {
                                id: "1234567890",
                                mealPlanId: "1",
                                type: "text",
                                content: "Leftovers",
                                notes: "This is a note",
                                category: "",
                                date: "2022-05-13"
                            }
                        ]
                    }
                },
                {
                    date: "2022-05-14",
                    dayOfWeek: "Saturday",
                    entries: {
                        "default": [
                            {
                                id: "1234567890",
                                mealPlanId: "1",
                                type: "text",
                                content: "Leftovers",
                                notes: "This is a note",
                                category: "",
                                date: "2022-05-14"
                            }
                        ]
                    }
                },
                {
                    date: "2022-05-15",
                    dayOfWeek: "Sunday",
                    entries: {
                        "default": [
                            {
                                id: "1234567890",
                                mealPlanId: "1",
                                type: "text",
                                content: "Leftovers",
                                notes: "This is a note",
                                category: "",
                                date: "2022-05-15"
                            }
                        ]
                    }
                }
            ]
        };

        this.state = {
            weekStartDate: "2022-05-19",
            entries: entries,
            loaded: false
        };
    }

    componentDidMount() {
        {/*fetch("/weekly-entries?mealplan_id={mealplanId}&date=")
            .then((res) => res.json())
            .then((json) => {
                this.setState({
                    entries: json,
                    loaded: true
                });
            })
        */}
    }

    renderDailyMealPlan(dayname, dailyentries) {
        return <DailyMealPlan dayname={dayname} entries={dailyentries} />;
    }

    render() {
        const weekHeader = 'Week of 2022-05-11';


        const mondayEntries    = this.state.entries.days[0].entries;
        const tuesdayEntries   = this.state.entries.days[1].entries;
        const wednesdayEntries = this.state.entries.days[2].entries;
        const thursdayEntries  = this.state.entries.days[3].entries;
        const fridayEntries    = this.state.entries.days[4].entries;
        const saturdayEntries  = this.state.entries.days[5].entries;
        const sundayEntries    = this.state.entries.days[6].entries;

        return (
            
            <div>
                <div className="week-header">{weekHeader}</div>
                <div className="weekly-mealplan-container">
                    {this.renderDailyMealPlan("Monday", mondayEntries)}
                    {this.renderDailyMealPlan("Tuesday", tuesdayEntries)}
                    {this.renderDailyMealPlan("Wednesday", wednesdayEntries)}
                    {this.renderDailyMealPlan("Thursday", thursdayEntries)}
                    {this.renderDailyMealPlan("Friday", fridayEntries)}
                    {this.renderDailyMealPlan("Saturday", saturdayEntries)}
                    {this.renderDailyMealPlan("Sunday", sundayEntries)}
                </div>
            </div>
        );
    }
}

export default WeeklyMealPlan;

