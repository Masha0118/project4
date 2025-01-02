

function loadProperties(filePath) {
    return fetch(filePath)
        .then(response => {
            if (!response.ok) {
                throw new Error(`프로퍼티 파일을 불러오는데 실패했습니다.: ${filePath}`)
            }
            return response.text();
        })
        .then(text => {
            const properties = {};
            text.split('\n').forEach(line => {
                const [key, value] = line.split('=').map(part => part.trim());
                if (key && value) {
                    properties[key] = value;
                }
            });
            return properties;
        })
        .catch(error => {
            console.error("로딩실패 or 변환 실패: ", error);
            throw error
        });
}


function loadRecaptcha(action, callback) {
    loadProperties('/resources/recaptcha.properties')
        .then(properties => {
            const siteKey = properties['recaptcha.site-key'];
            if (!siteKey) {
                throw new Error('사이트키를 찾을 수 없습니다.')
            }

            grecaptcha.ready(function () {
                grecaptcha.execute(siteKey, {action: action}).then(function (token) {
                    if (callback && typeof callback === 'function') {
                        callback(token);
                    }
                }).catch(function (error) {
                    console.error('reCaptcha 실행 실패: ', error)
                });
            }).catch(error => {
                console.error('loadRecaptcha 메서드 실패: ', error)
            });
        });
}


function rgreCaptcha(action) {
    return new Promise((resolve, reject) => {
        loadRecaptcha(action, function (token) {
            if (token) {
                resolve(token);
            } else {
                reject('리캡챠 토큰 생성 실패')
            }
        });
    });
}