const path = require('path')
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const TerserPlugin = require('terser-webpack-plugin');
const postcssPresetEnv = require('postcss-preset-env');
const Dotenv = require('dotenv-webpack');

const mode = process.env.NODE_ENV === 'production' ? 'production' : 'development'

module.exports = {
    entry: './src/index.js',
    module: {
        rules: [
            {
                test: /\.(s[ac]|c)ss$/i, // /\.(le|c)ss$/i если вы используете less
                use: [
                    MiniCssExtractPlugin.loader,
                    // 'style-loader',
                    'css-loader',
                    {
                        loader: 'postcss-loader',
                        options: {
                            postcssOptions: {
                                plugins: [postcssPresetEnv()],
                            },
                        },
                    },
                    'sass-loader',
                ],
            },
            {
                test: /\.js$/,
                exclude: /node_modules/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: ['@babel/preset-env'],
                        cacheDirectory: true,
                    },
                },
            },
            {test: /\.(html)$/, use: ['html-loader']},
            {test: /\.(png|jpe?g|gif|svg|webp|ico)$/i, type: mode === 'production' ? 'asset' : 'asset/resource',},
            {test: /\.(woff2?|eot|ttf|otf)$/i, type: 'asset/resource',},
        ]
    },
    output: {
        path: path.resolve(__dirname, 'dist'),
        assetModuleFilename: 'assets/[hash][ext][query]',
        filename: 'bundle-[hash].js',

        clean: true,
    },
    plugins: [
        new HtmlWebpackPlugin({
            template: './src/index.html',
        }),
        new MiniCssExtractPlugin({
            filename: '[name]-[hash].css',
        }),
        new Dotenv()
    ], // Создаем массив плагинов
    optimization: {
        minimize: true,
        minimizer: [
            new TerserPlugin({
                parallel: true,
                extractComments: false,
                terserOptions: {
                    format: {
                        comments: false,
                    },
                },
            }),
        ],
    },
    mode: mode,

    devtool: mode === 'development' ? 'source-map' : undefined,
    devServer: {
        hot: true,
    }
}