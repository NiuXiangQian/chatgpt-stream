if (typeof (EventSource) == "undefined") {
    alert("您的浏览器不支持SSE");
}

const onMessage = () => {
    let msg = $('.message-input').val()
    if (!msg) {
        alert('请输入内容')
        return
    }
    if (sendBtnIsLock()) {
        alert('请等待生成完毕')
        return
    }
    $('.chat-messages')
        .append(`<li> <div class="chat-message chat-question"><p>${msg}</p></div></li>`)
        .append(`<li> <div class="chat-message chat-reply"><p>&nbsp;</p></div></li>`)
    let last_message = $('.chat-message:last')
    let sse = new EventSource(`/openai/completions/stream?user=${getUser()}&prompt=${msg}`)
    $('.message-input').val('')
    lockSendBtn()
    sse.addEventListener('open', (function () {
        console.log('open')
        lockSendBtn()
    }))
    sse.addEventListener('message', function (res) {
        console.log('res', res)
        if (res.data === '[DONE]') {
            sse.close()
            unSendBtn()
        } else {
            last_message.find('p').append(`${res.data}`)
        }
    })

    sse.addEventListener('error', function () {
        console.log('error')
        last_message.find('p').append('<br/>服务器错误，请查看日志')
        alert('服务器错误，请查看日志')
        unSendBtn()
    })

}

function lockSendBtn() {
    $('.send-btn').attr('disabled', true)
}

function unSendBtn() {
    $('.send-btn').attr('disabled', false)
}

function sendBtnIsLock() {
    return $('.send-btn').attr('disabled') || false
}

function getUser() {
    return localStorage.getItem("user")
}

window.onload = () => {
    let user = getUser();
    if (!user) {
        console.log("No user found, creating new user")
        localStorage.setItem("user", getUuid())
    }
    console.log('user:', getUser())

    $('.send-btn').on('click', onMessage)
    $('.message-input').keypress(function (even) {
        if (even.which === 13) {
            onMessage()
        }
    })

}


function getUuid() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = (Math.random() * 16) | 0,
            v = c == 'x' ? r : (r & 0x3) | 0x8;
        return v.toString(16);
    });
}
