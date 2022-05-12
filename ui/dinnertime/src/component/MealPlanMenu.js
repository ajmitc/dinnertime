import React from 'react';

class MealPlanLink extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
        date: props.date
    };
  }

  handleClick() {
    const date = this.state.date;
    // TODO Request entries from this date to date+7days
    // TODO update Weekly Meal Plan
    console.log(date);
  }

  render() {
    return (
      <div className="mealplan-link">
        <button value={this.props.date} onClick={() => this.handleClick()}>
            {this.props.date}
        </button>
      </div>
    );
  }
}

class MealPlanMenu extends React.Component {
    constructor(props){
        super(props);

        this.state = {
            plans: [],
            plansLoaded: false,
            selectedPlan: undefined,
            dates: []
        };
    }
    componentDidMount() {
    {/*
        fetch("/mealplans")
            .then((res) => res.json())
            .then((json) => {
                this.setState({
                    plans: json,
                    plansLoaded: true,
                    selectedPlan: (Array.isArray(json) && json.length)? json[0]: null
                });
            })
        if (this.state.selectedplan){
            fetch("/mealplan-dates")
                .then((res) => res.json())
                .then((json) => {
                    this.setState({
                        dates: json
                    });
            })
        }
        */}
    }

    change(event){
        this.setState({selectedPlan: event.target.value});
    {/*
            fetch("/mealplan-dates")
                .then((res) => res.json())
                .then((json) => {
                    this.setState({
                        dates: json
                    });
            })
        */}
    }

    render() {
        {/*const mealplans = this.state.plans*/}
        const mealplans = [
            {
                id: "1",
                name: "Default",
                createdBy: "Aaron",
                createdOn: "2022-05-11",
                profileId: "1",
                permission: "read_write"
            }
        ];
        {/*const dates = this.state.dates*/}
        const dates = [
            "2022-05-09",
            "2022-05-08"
        ];

        return (
            <div className="mealplan-container">
                <div className="mealplan-dropdown-container">
                <span>Meal Plan:</span>
                    <select name="mealplan-dropdown" onChange={this.change} value={this.state.selectedPlan}>
                        {mealplans.map((plan) => <option key={plan.id} value={plan.id}>{plan.name}</option>)}
                    </select>
                </div>
                <div className="mealplan-menu-container">
                    {dates.map((date) => <MealPlanLink key={date} date={date} />)}
                </div>
            </div>
        );
    }
}

export default MealPlanMenu;

