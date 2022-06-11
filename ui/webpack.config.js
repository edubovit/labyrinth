const path = require('path')
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const TerserPlugin = require('terser-webpack-plugin');
const postcssPresetEnv = require('postcss-preset-env');
const Dotenv = require('dotenv-webpack');
const ReactRefreshWebpackPlugin = require('@pmmmwh/react-refresh-webpack-plugin');

const PRODUCTION = "production"
const DEVELOPMENT = "development"
const mode = process.env.NODE_ENV === PRODUCTION ? PRODUCTION : DEVELOPMENT


let plugins = [
    new HtmlWebpackPlugin({
        template: './src/index.html',
    }),
    new MiniCssExtractPlugin({
        filename: '[name]-[hash].css',
    }),
    new Dotenv()
]
let babel_plugins = []
if (mode === DEVELOPMENT) {
    plugins.push(new ReactRefreshWebpackPlugin());
    babel_plugins.push('react-refresh/babel');
}


module.exports = {
    entry: './src/index.js',
    module: {
        rules: [
            {
                test: /\.(s[ac]|c)ss$/i,
                use: [
                    MiniCssExtractPlugin.loader,
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
                test: /\.jsx?$/,
                exclude: /node_modules/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: ['@babel/preset-env', '@babel/preset-react'],
                        plugins: babel_plugins,
                        cacheDirectory: true,
                    },
                },
            },
            {test: /\.(html)$/, use: ['html-loader']},
            {test: /\.(png|jpe?g|gif|svg|webp|ico)$/i, type: mode === PRODUCTION ? 'asset' : 'asset/resource'},
            {test: /\.(woff2?|eot|ttf|otf)$/i, type: 'asset/resource',},
        ]
    },
    output: {
        path: path.resolve(__dirname, 'dist'),
        assetModuleFilename: 'assets/[hash][ext][query]',
        filename: 'bundle-[hash].js',

        clean: true,
    },
    plugins: plugins,
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

    devtool: mode === DEVELOPMENT ? 'source-map' : undefined,
    devServer: {
        hot: true,
    }
}