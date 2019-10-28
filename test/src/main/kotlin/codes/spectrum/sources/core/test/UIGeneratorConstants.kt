package codes.spectrum.sources.core.test

/**
 * Класс констант для генератора ui.html (также некоторые константы используются для Insomnia генератора)
 */
object UIGeneratorConstants {

    const val UI_HTML_PATH = "../service/src/main/resources/ui.html"

    const val INSOMNIA_JSON_PATH = "../insomnia.json"

    const val INSOMNIA_YAML_PATH = "../insomnia.yaml"

    const val GENERATE_ENV = "GENERATE_SOURCE_ARTEFACTS"

    const val INSOMNIA_EXPORT_TIME_ENV = "INSOMNIA_EXPORT_TIME"

    const val UI_DOCTYPE = "<!DOCTYPE html>"

    const val UI_HEAD_DEPENDENCIES =
        """        <meta charset="UTF-8">
        <title>Минимальный маппинг для включения в отчет</title>
        <script crossorigin src="https://unpkg.com/react@16/umd/react.development.js"></script>
        <script crossorigin src="https://unpkg.com/react-dom@16/umd/react-dom.development.js"></script>
        <script crossorigin src="https://unpkg.com/babel-standalone@6/babel.min.js"></script>
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"/>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <style> input[type="checkbox"] {height: 20px; width: 20px;} </style>"""

    const val UI_FORM_PARAMS_MARK = "%{FORM_PARAMS}"

    const val UI_QUERY_PARAMS_MARK = "%{QUERY_PARAMS}"

    const val UI_URL_CODE_MARK = "%{URL_CODE}"

    const val UI_URL = "${"\${url}"}/api/v1/sources/$UI_URL_CODE_MARK/cli/execute"

    const val UI_CASES_MARK = "%{CASES}"

    const val UI_SYSTEMS_MARK = "%{SYSTEMS}"

    const val UI_CASES_FORM_MARK = "%{FORM_CASES}"

    const val UI_DEFAULT_CASE_MARK = "%{DEFAULT_CASE_CODE}"

    const val UI_NONE_CASE_MARK = "%{NONE_CASE_CODE}"

    const val UI_REACT_CLASSES_MARK = "%{REACT_CLASSES}"

    const val UI_RESULT_DATA_MARK = "%{RESULT_DATA}"

    const val UI_CODES_LIST_MARK = "%{CODES_LIST}"

    const val UI_DEFAULT_RESULT_DATA =
"""{ !this.state.data.result.error && this.codesList.includes(this.state.data.result.status) &&
    <tr>
        <td>Ответ</td>
        <td>
            <pre>{JSON.stringify(this.state.data.result.data, null, 3)}</pre>
        </td>
    </tr>
}"""

    const val UI_CASES_LABEL = "Кейсы"

    const val UI_SYSTEMS_LABEL = "Системы"

    const val UI_SOURCE_TITLE_PREFIX = "Источник - "

    const val UI_SELF_FORM_PARAM_PREFIX = "self.state.form."

    const val UI_THIS_FORM_PARAM_PREFIX = "this.state.form."

    const val UI_INPUT_ID_SUFFIX = "_control"

    const val UI_CASE_INPUT_ID_PREFIX = "case"

    const val UI_SYSTEM_INPUT_ID_PREFIX = "sys"

    const val INDENT = "    "

    const val UI_QUERY_PARAMS_INDENT = 3

    const val UI_CASES_AND_SYSTEMS_INDENT = 3

    const val UI_RESULT_DATA_INDENT = 5

    const val UI_INSOMNIA_LINK_TEXT = "Доступ к Insomnia проекту"

    const val UI_INSOMNIA_JSON_TEXT = "JSON"

    const val UI_INSOMNIA_YAML_TEXT = "YAML"

    const val UI_INSOMNIA_JSON_LINK_PATH = "./insomnia.json"

    const val UI_INSOMNIA_YAML_LINK_PATH = "./insomnia.yaml"

    const val UI_DOC_DESCRIPTION_LINK = "Полную документацию можно посмотреть"

    const val UI_DOC_LINK_TEXT = "здесь"

    const val UI_DOC_LINK_PATH = "./doc"

    const val UI_REACT_SCRIPT = """
class SourceServicePage extends React.Component {
    constructor(props) {
        super(props);
        const baseurl = window.location.hostname;
        this.systems = {
$UI_SYSTEMS_MARK
        }
        const sys = (Object.entries(this.systems).find((e)=>e[1].url.includes(baseurl)) || ["RelativeRestSystem"])[0];
        this.defaultForm = {case:'', sys:sys, $UI_FORM_PARAMS_MARK};
        this.state = {form:{case:'', sys:sys, $UI_FORM_PARAMS_MARK}, data :null};
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    
        this.cases = {
$UI_CASES_MARK
        }
    }
    
    componentDidMount() {
        this.handleChange({name:"case",value:"$UI_DEFAULT_CASE_MARK"});
    }
    
    handleChange(event) {
        const {name,value} = event.target || event;
        const form = this.state.form;
        if (event.target && event.target.type == "checkbox") {
            form[name] = event.target.checked.toString();
        }
        else {
            form[name] = value;
        }
        if (name=="case") {
            const _case = this.cases[value];
            for(var p of Object.entries(this.state.form)) {
                if (p[0]=="sys") continue;
                if (p[0]=="case") continue;
                this.state.form[p[0]] = _case[p[0]] || this.defaultForm[p[0]];
                let input = document.getElementById(p[0] + "$UI_INPUT_ID_SUFFIX");
                if (input.type == "checkbox") {
                    input.checked = this.state.form[p[0]]=='true';
                }
            }
        } else if (name!='sys') {
            form["case"] = "$UI_NONE_CASE_MARK";
        }
        this.setState({form});
        if (name=='case' || name=='sys') {
            this.handleSubmit();
        }
    }
    
    prepareQuery() {
        let self = this;
        var result = {
            caseCode:self.state.form.case,
$UI_QUERY_PARAMS_MARK
        }
        return result;
    }
    
    handleSubmit(event) {
        const self = this;
        event && event.preventDefault();
        const url = this.systems[this.state.form.sys].url;
        fetch(`$UI_URL`, {
            method:"POST",
            headers: {
            'Accept': 'application/json;charset=utf-8',
            'Content-Type': 'application/json;charset=utf-8'
        },
            body: JSON.stringify(this.prepareQuery())
        })
        .then(function(response) {
            return response.json();
        })
        .then(function(myJson) {
            self.setState({data:myJson})
        });
    }
    
    render() {
        return (
            <div className="row">
                <div className="col-2 bg-light p-3">
                    <form onSubmit={this.handleSubmit}>
$UI_CASES_FORM_MARK
                        <div>
                            <input type="submit" value="Выполнить запрос" />
                        </div>
                    </form>
                </div>
                <div id="example" className="col-6" >
                    {this.state.data && new SourceBlock(this.cases[this.state.form["case"]], this.state).render()}
                </div>
            </div>
        );
    }
}

$UI_REACT_CLASSES_MARK
    
class SourceBlock {
    constructor(_case, state){
        if (_case === undefined || _case.description === undefined) {
            this.caseDescription = "Описание отсутствует";
        }
        else {
            this.caseDescription = _case.description;
        }
        this.state = state;
        this.codesList = $UI_CODES_LIST_MARK;
    }
    
    render() {
        return (
            <div className="results col">
                <h4>Описание кейса</h4>
                { <div dangerouslySetInnerHTML={{ __html: this.caseDescription }}/> }
                <h2>Результаты обработки</h2>
                <table>
                    <tbody>
                    <tr>
                        <td>Статус</td>
                        <td>{this.state.data.result.status}</td>
                    </tr>
$UI_RESULT_DATA_MARK
                    { this.state.data.result.error && 
                        <tr>
                            <td>Ошибка</td>
                            <td><pre>{JSON.stringify(this.state.data.result.error, null, 3)}</pre></td>
                        </tr>
                    }
                    </tbody>
                </table>
            </div>
        );
    }
}
    
ReactDOM.render(
    <SourceServicePage/>,
    document.getElementById("source-page")
);"""
}