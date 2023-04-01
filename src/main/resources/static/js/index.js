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
    $('.chat-message-container')
        .append(`<li> <div class="chat-message chat-question"><p>${msg}</p></div></li>`)
        .append(`<li> <div class="chat-message chat-reply"><p></p></div></li>`)
    let last_message = $('.chat-message:last')
    let sse = new EventSource(`/openai/completions/stream?user=${getUser()}&prompt=${msg}`)
    $('.message-input').val('')
    lockSendBtn()
    sse.addEventListener('open', (function () {
        console.log('open')
        lockSendBtn()
    }))
    sse.addEventListener('message', function (res) {
            let resJson = JSON.parse(res.data)
            console.log('resJson', resJson)
            if (resJson.messageType === 'TEXT') {
                if (resJson.end === true) {
                    sse.close()
                    unSendBtn()
                } else {
                    if (resJson.message.indexOf('\n') > -1) {
                        let line = resJson.message.split('\n');
                        let p_last = last_message.find('p:last')
                        for (let i = 0; i < line.length - 1; i++) {
                            if (line[i]){
                                p_last.append(`${resJson.message}`)
                            }
                            last_message.append(`<p>&nbsp;</p>`)
                        }
                        return
                    }
                    last_message.find('p:last').append(`${resJson.message}`)
                }
                //图片
            } else {
                resJson.message.split(",").forEach(url => {
                    last_message.find('p').append(`<img class="chat-img" src="${url}"/>`)
                })
                sse.close()
                unSendBtn()
            }

        }
    )

    sse.addEventListener('error', function () {
        console.log('error')
        last_message.find('p').append('<br/>服务器错误，请查看日志')
        alert('服务器错误，请查看日志')
        unSendBtn()
    })

}

function lockSendBtn() {
    $('.send-btn').attr('disabled', true)
    // 动画滚动到页面底部
    $('html, body').animate({scrollTop: $(document).height()}, 'slow');
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
